package info.esuarez.springhibernatedemo.queries;

import info.esuarez.springhibernatedemo.entity.Instructor;
import info.esuarez.springhibernatedemo.entity.InstructorDetail;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class InstructorQueries {

    private Instructor instructor1 = new Instructor("Chad", "Darby", "darby@luv2code.com");
    private Instructor instructor2 = new Instructor("Madhu", "Patel", "madhu@luv2code.com");

    private InstructorDetail instructorDetail1 =
            new InstructorDetail("http://www.luv2code.com/youtube", "Luv 2 code!!");
    private InstructorDetail instructorDetail2 =
            new InstructorDetail("http://www.youtube.com/", "Guitar");

    private SessionFactory factory;

    public InstructorQueries(SessionFactory factory) {
        this.factory = factory;
        instructor1.setInstructorDetail(instructorDetail1);
        instructor2.setInstructorDetail(instructorDetail2);
    }

    public void saveInstructor() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            session.save(instructor1);
            session.save(instructor2);
            session.getTransaction().commit();
        }
    }

    public void reverseLookup() {
        int id = 2;
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            InstructorDetail instructorDetail = session.get(InstructorDetail.class, id);
            session.getTransaction().commit();

            System.out.println("Reverse lookup Instructor detail: " + instructorDetail);
            System.out.println("Reverse lookup: " + instructorDetail.getInstructor());
        }
    }

    public void deleteInstructor() {
        int id = 1;
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            Instructor instructor = session.get(Instructor.class, id);
            session.delete(instructor);
            session.getTransaction().commit();
        }
    }


    public void dropInstructorTable() {
        try (Session session = factory.getCurrentSession()) {

            session.beginTransaction();
            session.createSQLQuery("DROP TABLE IF EXISTS course, instructor, instructor_detail, hibernate_sequence")
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }
}
