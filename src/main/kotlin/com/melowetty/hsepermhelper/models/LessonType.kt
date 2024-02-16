package com.melowetty.hsepermhelper.models

enum class LessonType(val type: String, private val scheduleFilePattern: String) {
    LECTURE("Лекция", "{type}: {subject}") {
        override fun reformatSubject(subject: String): String {
            return subject
                .replace("(лекция)", "")
                .replace("(лекции)", "")
                .trim()
        }
    },
    SEMINAR("Семинар", "{type}: {subject}") {
        override fun reformatSubject(subject: String): String {
            return subject.replace("(семинар)", "").trim()
        }
    },
    EXAM("Экзамен", "{type}: {subject}") {
        override fun reformatSubject(subject: String): String {
            return subject.replace("ЭКЗАМЕН", "").trim()
        }
    },
    INDEPENDENT_EXAM("Независимый экзамен", "{subject}"),
    TEST("Зачёт", "{type}: {subject}") {
        override fun reformatSubject(subject: String): String {
            return subject.replace("ЗАЧЕТ", "").trim()
        }
    },
    PRACTICE("Практика", "{type}: {subject}") {
        override fun reformatSubject(subject: String): String {
            return if (subject == "ПРАКТИКА") ""
            else subject
        }
    },
    COMMON_MINOR("Майнор", "{type}"),
    MINOR("Майнор", "{type}"),
    COMMON_ENGLISH("Английский", "{type}"),
    ENGLISH("Английский", "{type}"),
    STATEMENT("Ведомость", "{type}: {subject}"),
    ICC("МКД", "{type}: {subject}"),
    UNDEFINED_AED("ДОЦ по выбору", "{subject}"),
    AED("ДОЦ", "{subject}") {
        override fun reformatSubject(subject: String): String {
            return subject
                .replace("[", "")
                .replace("]", "")
                .trim()
        }
    },
    CONSULT("Консультация", "{subject}"),
    EVENT("Мероприятие", "{type}: {subject}");
    fun toEventSubject(subject: String): String {
        return scheduleFilePattern
            .replace("{type}", type)
            .replace("{subject}", subject)
    }
    public open fun reformatSubject(subject: String): String {
        return subject
    }
}