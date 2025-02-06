package com.melowetty.hsepermhelper.model.lesson

enum class LessonType(val type: String) {
    LECTURE("Лекция") {
        override fun reformatSubject(subject: String): String {
            return subject
                .replace("(лекция)", "")
                .replace("(лекции)", "")
                .replace("(лек.)", "")
                .trim()
        }
    },
    SEMINAR("Семинар") {
        override fun reformatSubject(subject: String): String {
            return subject
                .replace("(семинар)", "")
                .replace("(практ.)", "")
                .trim()
        }
    },
    EXAM("Экзамен") {
        override fun reformatSubject(subject: String): String {
            return subject.replace("ЭКЗАМЕН", "").trim()
        }
    },
    INDEPENDENT_EXAM("Независимый экзамен"),
    TEST("Зачёт") {
        override fun reformatSubject(subject: String): String {
            return subject.replace("ЗАЧЕТ", "").trim()
        }
    },
    PRACTICE("Практика") {
        override fun reformatSubject(subject: String): String {
            return if (subject == "ПРАКТИКА") ""
            else subject
        }
    },
    COMMON_MINOR("Майнор"),
    COMMON_ENGLISH("Английский"),
    ENGLISH("Английский"),
    STATEMENT("Ведомость"),
    ICC("МКД") {
        override fun reformatSubject(subject: String): String {
            return subject
                .replace("(МКД)", "")
                .trim()
        }
    },
    UNDEFINED_AED("ДОЦ по выбору"),
    AED("ДОЦ") {
        override fun reformatSubject(subject: String): String {
            return subject
                .replace("[", "")
                .replace("]", "")
                .trim()
        }
    },
    CONSULT("Консультация"),
    EVENT("Мероприятие");

    open fun reformatSubject(subject: String): String {
        return subject
    }
}