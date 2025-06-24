package com.mdtalalwasim.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DeliveryAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private ProductOrder order;

    private String deliveryFirstName;   // Nombre del repartidor
    private String deliveryLastName;    // Apellido del repartidor
    private String deliveryPhone;       // Teléfono de contacto
    private String deliveryApp;         // Aplicación: Uber, InDrive, etc.
    private String deliveryStatus;      // Estado: ASIGNADO, EN CAMINO, ENTREGADO

    public String getDeliveryName() {
        return deliveryFirstName + " " + deliveryLastName;
    }
}
