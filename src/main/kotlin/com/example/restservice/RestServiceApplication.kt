package com.example.restservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentHashMap

@SpringBootApplication
class HwProjServiceApplication

fun main(args: Array<String>) {
    runApplication<HwProjServiceApplication>(*args)
}

@RestController
class RestController(val hwProjService: HwProjService) {

    @PostMapping(value = ["/students/{student}/register/{subject}"])
    fun registerStudentOnSubject(@PathVariable student: String, @PathVariable subject: String) =
        hwProjService.registerStudentOnSubject(student, subject)

    @GetMapping(value = ["/students/{student}/marks"])
    fun getStudentMarks(@PathVariable student: String) =
        hwProjService.getMarks(student)

    @GetMapping(value = ["/students/{student}/marks/{subject}"])
    fun getStudentMarksOnSubject(@PathVariable student: String, @PathVariable subject: String) =
        hwProjService.getMarksFromSubject(student, subject)

    @GetMapping(value = ["/students/{student}/marks/{subject}/avg"])
    fun getStudentMarksOnSubjectAvg(@PathVariable student: String, @PathVariable subject: String) =
        hwProjService.getMarksFromSubjectAvg(student, subject)

    @PostMapping(value = ["/teachers/{teacher}/marks/{student}/{mark}"])
    fun setMarkToStudent(@PathVariable teacher: String,
                         @PathVariable student: String,
                         @PathVariable mark: Int) = hwProjService.setMarkToStudent(teacher, student, mark)

    @PostMapping(value = ["/teachers/{teacher}/register/{subject}"])
    fun addSubject(@PathVariable teacher: String,
                         @PathVariable subject: String) = hwProjService.addSubject(teacher, subject)

    @GetMapping(value = ["/teachers/{teacher}/marks/"])
    fun getMarksByTeacher(@PathVariable teacher: String) =
        hwProjService.getMarksByTeacher(teacher)

}


@Service
class HwProjService {
    private val students = ConcurrentHashMap<String, Student>()
    private val teachers = ConcurrentHashMap<String, Teacher>()

    fun getMarks(studentName: String): Map<String, List<Int>>? {
        return students[studentName]?.subjectsToMark
    }

    fun getMarksFromSubject(studentName: String, subjectName: String): List<Int>? {
        return getMarks(studentName)?.get(subjectName)
    }

    fun getMarksFromSubjectAvg(studentName: String, subjectName: String): Double? {
        return getMarks(studentName)?.get(subjectName)?.average()
    }

    fun registerStudentOnSubject(studentName: String, subject: String) {
        students.computeIfAbsent(studentName) { Student(studentName) }
            .subjectsToMark.computeIfAbsent(subject) { mutableListOf()}
    }

    fun setMarkToStudent(teacherName: String,
                         studentName: String,
                         mark: Int) {
        teachers[teacherName]?.let { students[studentName]?.subjectsToMark?.get(it.subjectName)?.add(mark) }
    }

    fun addSubject(teacherName: String, subjectName: String) {
        teachers[teacherName] = Teacher(teacherName, subjectName)
    }

    fun getMarksByTeacher(teacherName: String): Map<String, List<Int>?> {
        val data: MutableMap<String, MutableList<Int>?> = mutableMapOf()
        val subjectName = teachers[teacherName]?.subjectName
        for (student in students.values) {
            if (student.subjectsToMark.contains(subjectName)) {
                data[student.name] = student.subjectsToMark[subjectName]
            }
        }
        return data
    }

}


data class Student(
    val name: String,
    val subjectsToMark: ConcurrentHashMap<String, MutableList<Int>> = ConcurrentHashMap<String, MutableList<Int>>()
)

data class Teacher(
    val name: String,
    val subjectName: String
)
