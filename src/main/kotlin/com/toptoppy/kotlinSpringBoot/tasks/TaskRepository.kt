package com.toptoppy.kotlinSpringBoot.tasks

import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository: JpaRepository<TaskEntity, Long>