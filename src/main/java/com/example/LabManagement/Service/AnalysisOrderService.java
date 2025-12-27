package com.example.LabManagement.Service;

import com.example.LabManagement.AnalysisStatus;
import com.example.LabManagement.Entity.AnalysisOrder;
import com.example.LabManagement.Entity.ExaminationType;
import com.example.LabManagement.Entity.Visit;
import com.example.LabManagement.Exception.AnalysisOrderNotFoundException;
import com.example.LabManagement.Exception.VisitNotFoundException;
import com.example.LabManagement.Repository.AnalysisOrderRepository;
import com.example.LabManagement.Repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisOrderService {

    private final AnalysisOrderRepository analysisOrderRepository;
    private final VisitRepository visitRepository;

    /**
     * Create a new analysis order for a specific visit
     */
    @Transactional
    public AnalysisOrder createAnalysisOrder(Long visitId, ExaminationType examType) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with ID " + visitId));

        AnalysisOrder order = new AnalysisOrder();
        order.setVisit(visit);
        order.setPatient(visit.getPatient());
        order.setDoctor(visit.getDoctor());
        order.setExaminationType(examType);
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(AnalysisStatus.PENDING);  // use your enum

        return analysisOrderRepository.save(order);
    }
    
    /**
     * Get all pending analysis orders (for lab technicians)
     */
    public List<AnalysisOrder> getPendingOrders() {
        return analysisOrderRepository.findByStatus(AnalysisStatus.PENDING);
    }
    
    /**
     * Get all orders for a specific patient
     */
    public List<AnalysisOrder> getOrdersByPatientId(Long patientId) {
        return analysisOrderRepository.findByPatientId(patientId);
    }
    
    /**
     * Create a patient-requested analysis order (without visit/doctor - needs approval)
     */
    @Transactional
    public AnalysisOrder createPatientRequestedOrder(AnalysisOrder order) {
        order.setOrderedAt(LocalDateTime.now());
        order.setStatus(AnalysisStatus.PENDING);
        return analysisOrderRepository.save(order);
    }


    /**
     * Find an analysis order by ID
     */
    public AnalysisOrder findOrderById(Long id) {
        return analysisOrderRepository.findById(id)
                .orElseThrow(() -> new AnalysisOrderNotFoundException("Analysis order not found with ID " + id));
    }

    /**
     * Get all pending patient-requested orders (without doctor assignment)
     */
    public List<AnalysisOrder> getPendingPatientRequests() {
        return analysisOrderRepository.findByStatusAndDoctorIsNull(AnalysisStatus.PENDING);
    }
    
    /**
     * Get all orders for a specific doctor
     */
    public List<AnalysisOrder> getOrdersByDoctorId(Long doctorId) {
        return analysisOrderRepository.findByDoctorIdAndStatus(doctorId, AnalysisStatus.PENDING);
    }
    
    /**
     * Approve a patient-requested analysis order by assigning a doctor
     */
    @Transactional
    public AnalysisOrder approvePatientRequest(Long orderId, Long doctorId) {
        AnalysisOrder order = findOrderById(orderId);
        if (order.getDoctor() != null) {
            throw new RuntimeException("Order already assigned to a doctor");
        }
        // Doctor assignment will be done in controller
        return order;
    }
    
    /**
     * Get all analysis orders
     */
    public List<AnalysisOrder> getAllOrders() {
        return analysisOrderRepository.findAll();
    }

    /**
     * Get all orders for a specific visit
     */
    public List<AnalysisOrder> getOrdersByVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new VisitNotFoundException("Visit not found with ID " + visitId));

        return visit.getAnalysisOrders();
    }

    /**
     * Update an analysis order (type, notes, scheduled date, status)
     */
    @Transactional
    public AnalysisOrder updateOrder(Long orderId, ExaminationType examinationType,
                                     LocalDateTime completedAt, AnalysisStatus status) {
        AnalysisOrder order = findOrderById(orderId);

        if (examinationType != null) order.setExaminationType(examinationType);
        if (completedAt != null) order.setCompletedAt(completedAt);
        if (status != null) order.setStatus(status);

        return analysisOrderRepository.save(order);
    }


    /**
     * Delete an analysis order
     */
    @Transactional
    public void deleteOrder(Long orderId) {
        AnalysisOrder order = findOrderById(orderId);
        analysisOrderRepository.delete(order);
    }

    /**
     * Total analysis orders count
     */
    public long getTotalOrderCount() {
        return analysisOrderRepository.count();
    }
}
