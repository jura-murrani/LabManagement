package com.example.LabManagement.Service;

import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.AnalysisResult;
import com.example.LabManagement.Entity.ExaminationTemplate;
import com.example.LabManagement.Entity.ExaminationTemplateField;
import com.example.LabManagement.Entity.LabTech;
import com.example.LabManagement.Entity.ResultField;
import com.example.LabManagement.Exception.AnalysisOrderNotFoundException;
import com.example.LabManagement.Exception.AnalysisResultNotFoundException;
import com.example.LabManagement.Exception.LabTechNotFoundException;
import com.example.LabManagement.Repository.AnalysisOrderRepository;
import com.example.LabManagement.Repository.AnalysisResultRepository;
import com.example.LabManagement.Repository.LabTechRepository;
import com.example.LabManagement.Repository.ResultFieldRepository;
import com.example.LabManagement.Repository.ExaminationTemplateFieldRepository;
import com.example.LabManagement.Repository.ExaminationTemplateRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisResultService {

    private final AnalysisResultRepository analysisResultRepository;
    private final AnalysisOrderRepository analysisOrderRepository;
    private final LabTechRepository labTechRepository;
    private final ResultFieldRepository resultFieldRepository;
    private final ExaminationTemplateRepository templateRepository;
    private final ExaminationTemplateFieldRepository templateFieldRepository;

    /**
     * Create a new AnalysisResult linked to an AnalysisOrder and LabTech
     * This marks the analysis as completed by lab tech (status updated to COMPLETED)
     */
    @Transactional
    public AnalysisResult createResult(Long analysisOrderId, Long labTechId,
                                       String resultData, String comment, String unit, String referenceRange) {
        AnalysisOrder order = analysisOrderRepository.findById(analysisOrderId)
                .orElseThrow(() -> new AnalysisOrderNotFoundException("AnalysisOrder not found with ID " + analysisOrderId));

        LabTech labTech = labTechRepository.findById(labTechId)
                .orElseThrow(() -> new LabTechNotFoundException("LabTech not found with ID " + labTechId));

        AnalysisResult result = new AnalysisResult();
        result.setAnalysisOrder(order);
        result.setLabTech(labTech);
        result.setResultData(resultData);
        result.setComment(comment);
        result.setUnit(unit);
        result.setReferenceRange(referenceRange);
        result.setIsReadyForPatient(false); // Not ready until doctor reviews

        // Update order status to COMPLETED
        order.setStatus(com.example.LabManagement.AnalysisStatus.COMPLETED);
        order.setCompletedAt(java.time.LocalDateTime.now());
        analysisOrderRepository.save(order);

        return analysisResultRepository.save(result);
    }

    /**
     * Create a new AnalysisResult with multiple field results from template
     */
    @Transactional
    public AnalysisResult createResultWithFields(Long analysisOrderId, Long labTechId,
                                                 java.util.Map<String, String> fieldResults,
                                                 String comment,
                                                 java.util.Map<String, String> fieldUnits,
                                                 java.util.Map<String, String> fieldReferenceRanges) {
        AnalysisOrder order = analysisOrderRepository.findById(analysisOrderId)
                .orElseThrow(() -> new AnalysisOrderNotFoundException("AnalysisOrder not found with ID " + analysisOrderId));

        LabTech labTech = labTechRepository.findById(labTechId)
                .orElseThrow(() -> new LabTechNotFoundException("LabTech not found with ID " + labTechId));

        AnalysisResult result = new AnalysisResult();
        result.setAnalysisOrder(order);
        result.setLabTech(labTech);
        result.setComment(comment);
        result.setIsReadyForPatient(false);
        result.setResultFields(new java.util.ArrayList<>());

        // Save result first to get ID
        result = analysisResultRepository.save(result);

        // Add result fields
        for (java.util.Map.Entry<String, String> entry : fieldResults.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                ResultField resultField = new ResultField();
                resultField.setFieldName(entry.getKey());
                resultField.setResultValue(entry.getValue().trim());
                String unit = (fieldUnits != null && fieldUnits.containsKey(entry.getKey())) ? fieldUnits.get(entry.getKey()) : null;
                String range = (fieldReferenceRanges != null && fieldReferenceRanges.containsKey(entry.getKey())) ? fieldReferenceRanges.get(entry.getKey()) : null;
                resultField.setUnit(unit);
                resultField.setReferenceRange(range);
                resultField.setAnalysisResult(result);
                resultField = resultFieldRepository.save(resultField);
                result.getResultFields().add(resultField);
            }
        }
        
        // Ensure at least one field was saved
        if (result.getResultFields().isEmpty()) {
            throw new RuntimeException("At least one result field must be provided");
        }

        // Update order status to COMPLETED
        order.setStatus(com.example.LabManagement.AnalysisStatus.COMPLETED);
        order.setCompletedAt(java.time.LocalDateTime.now());
        analysisOrderRepository.save(order);

        return analysisResultRepository.save(result);
    }

    /**
     * Update an existing AnalysisResult (used by lab tech)
     */
    @Transactional
    public AnalysisResult updateResult(Long resultId, String resultData, String comment,
                                       String unit, String referenceRange) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new AnalysisResultNotFoundException("AnalysisResult not found with ID " + resultId));

        if (resultData != null) result.setResultData(resultData);
        if (comment != null) result.setComment(comment);
        if (unit != null) result.setUnit(unit);
        if (referenceRange != null) result.setReferenceRange(referenceRange);

        return analysisResultRepository.save(result);
    }
    
    /**
     * Add doctor notes to a result and mark as ready for patient if both lab tech and doctor are done
     */
    @Transactional
    public AnalysisResult addDoctorNotes(Long resultId, String doctorNotes) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new AnalysisResultNotFoundException("AnalysisResult not found with ID " + resultId));
        
        result.setDoctorNotes(doctorNotes);
        // Mark as ready for patient once doctor adds notes (assuming lab tech already completed it)
        result.setIsReadyForPatient(true);
        
        return analysisResultRepository.save(result);
    }
    
    /**
     * Get all results ready for a specific patient to view
     */
    public List<AnalysisResult> getResultsReadyForPatient(Long patientId) {
        List<AnalysisResult> results = analysisResultRepository.findByAnalysisOrder_PatientIdAndIsReadyForPatientTrue(patientId);
        // Load result fields for each result
        for (AnalysisResult result : results) {
            result.setResultFields(resultFieldRepository.findByAnalysisResultId(result.getId()));
            backfillFieldMetadata(result);
        }
        return results;
    }
    
    /**
     * Get all results for a patient (for doctors to view)
     */
    public List<AnalysisResult> getResultsByPatientId(Long patientId) {
        List<AnalysisResult> results = analysisResultRepository.findByAnalysisOrder_PatientId(patientId);
        // Load result fields for each result
        for (AnalysisResult result : results) {
            result.setResultFields(resultFieldRepository.findByAnalysisResultId(result.getId()));
            backfillFieldMetadata(result);
        }
        return results;
    }

    /**
     * Get AnalysisResult by ID
     */
    public AnalysisResult getResultById(Long resultId) {
        AnalysisResult result = analysisResultRepository.findById(resultId)
                .orElseThrow(() -> new AnalysisResultNotFoundException("AnalysisResult not found with ID " + resultId));
        // Load result fields
        result.setResultFields(resultFieldRepository.findByAnalysisResultId(resultId));
        backfillFieldMetadata(result);
        return result;
    }

    /**
     * Get all AnalysisResults
     */
    public List<AnalysisResult> getAllResults() {
        return analysisResultRepository.findAll();
    }

    /**
     * Delete an AnalysisResult
     */
    @Transactional
    public void deleteResult(Long resultId) {
        AnalysisResult result = getResultById(resultId);
        analysisResultRepository.delete(result);
    }

    /**
     * Find AnalysisResult by AnalysisOrder
     */
    public AnalysisResult getResultByOrder(Long analysisOrderId) {
        AnalysisResult result = analysisResultRepository.findByAnalysisOrderId(analysisOrderId)
                .orElseThrow(() -> new AnalysisResultNotFoundException("Result for order " + analysisOrderId + " not found"));
        result.setResultFields(resultFieldRepository.findByAnalysisResultId(result.getId()));
        backfillFieldMetadata(result);
        return result;
    }
    
    /**
     * Find AnalysisResult by AnalysisOrder (returns null if not found)
     */
    public AnalysisResult getResultByOrderOrNull(Long analysisOrderId) {
        AnalysisResult result = analysisResultRepository.findByAnalysisOrderId(analysisOrderId).orElse(null);
        if (result != null) {
            result.setResultFields(resultFieldRepository.findByAnalysisResultId(result.getId()));
            backfillFieldMetadata(result);
        }
        return result;
    }

    /**
     * Fill missing unit/reference range on ResultField from the examination template, if available.
     */
    private void backfillFieldMetadata(AnalysisResult result) {
        if (result == null || result.getAnalysisOrder() == null ||
                result.getAnalysisOrder().getExaminationType() == null) {
            return;
        }

        Long typeId = result.getAnalysisOrder().getExaminationType().getId();
        if (typeId == null) return;

        ExaminationTemplate template = templateRepository.findByExaminationTypeId(typeId).orElse(null);
        if (template == null || template.getId() == null) return;

        java.util.List<ExaminationTemplateField> tmplFields =
                templateFieldRepository.findByTemplateIdOrderByDisplayOrderAsc(template.getId());
        if (tmplFields == null || tmplFields.isEmpty()) return;

        java.util.Map<String, ExaminationTemplateField> byName = new java.util.HashMap<>();
        for (ExaminationTemplateField f : tmplFields) {
            if (f.getFieldName() != null) {
                byName.put(f.getFieldName(), f);
            }
        }

        if (result.getResultFields() == null) return;

        for (ResultField rf : result.getResultFields()) {
            ExaminationTemplateField tf = byName.get(rf.getFieldName());
            if (tf == null) continue;
            if (rf.getUnit() == null || rf.getUnit().trim().isEmpty()) {
                rf.setUnit(tf.getUnit());
            }
            if (rf.getReferenceRange() == null || rf.getReferenceRange().trim().isEmpty()) {
                rf.setReferenceRange(tf.getReferenceRange());
            }
        }
    }
}
