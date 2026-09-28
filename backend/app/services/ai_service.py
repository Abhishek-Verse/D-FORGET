import json
import httpx
from pydantic import ValidationError
from app.core.config import settings
from app.schemas.ai import AnalysisResult


class AIConfigurationError(RuntimeError):
    pass


class AIProviderError(RuntimeError):
    pass

class AIService:
    API_URL = "https://api.openai.com/v1/chat/completions"

    @staticmethod
    async def analyze_text(text: str, source_id: int) -> AnalysisResult:
        api_key = settings.AI_API_KEY.strip()
        if not api_key or api_key == "your_api_key_here":
            raise AIConfigurationError(
                "Set AI_API_KEY in backend/.env to enable AI analysis."
            )

        request_body = {
            "model": settings.AI_MODEL,
            "response_format": {"type": "json_object"},
            "messages": [
                {
                    "role": "system",
                    "content": (
                        "Extract actionable tasks from the supplied source. "
                        "Return one JSON object with keys summary (string), "
                        "action_required (boolean), tasks (array), and "
                        "confidence_score (number from 0 to 1). Each task must "
                        "have title, description, deadline, priority, category, "
                        "and subject. Use ISO 8601 for a clearly stated deadline; "
                        "otherwise use null. Never invent a deadline or task. "
                        "If a date is ambiguous, use null. Priority must be one "
                        "of low, medium, high, or urgent. If no action is needed, "
                        "return action_required=false and an empty tasks array."
                    ),
                },
                {"role": "user", "content": text},
            ],
        }

        try:
            async with httpx.AsyncClient(timeout=60.0) as client:
                response = await client.post(
                    AIService.API_URL,
                    headers={"Authorization": f"Bearer {api_key}"},
                    json=request_body,
                )
                response.raise_for_status()
        except httpx.TimeoutException as exc:
            raise AIProviderError("The AI provider request timed out.") from exc
        except httpx.HTTPStatusError as exc:
            raise AIProviderError(
                f"The AI provider returned HTTP {exc.response.status_code}. "
                "Check the API key, model, and account quota."
            ) from exc
        except httpx.RequestError as exc:
            raise AIProviderError("Could not connect to the AI provider.") from exc

        try:
            content = response.json()["choices"][0]["message"]["content"]
            result = json.loads(content)
            result["source_id"] = source_id
            return AnalysisResult.model_validate(result)
        except (IndexError, KeyError, TypeError, ValueError, ValidationError) as exc:
            raise AIProviderError(
                "The AI provider returned a response in an unexpected format."
            ) from exc
