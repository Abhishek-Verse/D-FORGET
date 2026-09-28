from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from app.database import models
from app.database.connection import get_db
from app.schemas import ai as schemas
from app.services.ai_service import AIConfigurationError, AIProviderError, AIService

router = APIRouter(
    prefix="/analyze",
    tags=["analysis"]
)

@router.post("/{source_id}", response_model=schemas.AnalysisResult)
async def analyze_source(source_id: int, db: Session = Depends(get_db)):
    db_source = db.query(models.Source).filter(models.Source.id == source_id).first()
    if db_source is None:
        raise HTTPException(status_code=404, detail="Source not found")

    if not db_source.original_text:
        raise HTTPException(
            status_code=400,
            detail="Source has no extracted text to analyze. Ensure OCR or PDF extraction succeeded."
        )

    # Trigger AI Analysis
    try:
        result = await AIService.analyze_text(db_source.original_text, source_id)
    except AIConfigurationError as exc:
        raise HTTPException(status_code=503, detail=str(exc)) from exc
    except AIProviderError as exc:
        raise HTTPException(status_code=502, detail=str(exc)) from exc

    # Optionally update the source with a summary
    db_source.summary = result.summary
    db.commit()

    return result
