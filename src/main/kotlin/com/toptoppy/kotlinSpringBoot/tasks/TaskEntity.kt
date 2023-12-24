package com.toptoppy.kotlinSpringBoot.tasks

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
data class TaskEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long? = 0,
    var title: String,
    var description: String,
    var dueDate: Instant,
    var status: String,
    @CreationTimestamp
    @Column(updatable = false)
    var createAt: Instant? = null,
    @UpdateTimestamp
    var updateAt: Instant? = null
)


