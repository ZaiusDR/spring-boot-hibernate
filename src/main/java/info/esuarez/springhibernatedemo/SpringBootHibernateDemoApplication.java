package info.esuarez.springhibernatedemo;

import info.esuarez.springhibernatedemo.entity.Course;
import info.esuarez.springhibernatedemo.entity.Instructor;
import info.esuarez.springhibernatedemo.entity.InstructorDetail;
import info.esuarez.springhibernatedemo.entity.Student;
import info.esuarez.springhibernatedemo.queries.InstructorQueries;
import info.esuarez.springhibernatedemo.queries.StudentQueries;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringBootHibernateDemoApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringBootHibernateDemoApplication.class, args);

        SessionFactory factory = new Configuration()
                .configure()
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Instructor.class)
                .addAnnotatedClass(InstructorDetail.class)
                .addAnnotatedClass(Course.class)
                .buildSessionFactory();

        StudentQueries studentQueries = new StudentQueries(factory);
        InstructorQueries instructorQueries = new InstructorQueries(factory);

        executeStudentQueries(studentQueries);
        executeInstructorQueries(instructorQueries);

        // cleanDB(studentQueries, instructorQueries);

    }

    public static void executeStudentQueries(StudentQueries studentQueries) {
        studentQueries.insertStudents();
        studentQueries.getSimpleStudent();
        studentQueries.getStudentsWithLastName();
        studentQueries.updateStudent();
        studentQueries.updateAllStudents();
        studentQueries.deleteStudent();
        studentQueries.deleteAllStudents();
        //studentQueries.dropStudentTable();
    }

    public static void executeInstructorQueries(InstructorQueries instructorQueries) {
        instructorQueries.saveInstructor();
        instructorQueries.reverseLookup();
        instructorQueries.deleteInstructor();

        // instructorQueries.dropInstructorTable();
    }

    public static void cleanDB(StudentQueries studentQueries, InstructorQueries instructorQueries) {
        studentQueries.dropStudentTable();
        // instructorQueries.dropInstructorTable();
    }

}
