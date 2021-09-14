package com.larissa.reactiveprogrammingrxjava2.module5;

import com.larissa.reactiveprogrammingrxjava2.utility.ThreadHelper;
import com.larissa.reactiveprogrammingrxjava2.utility.subjects.NamedSubject;
import com.larissa.reactiveprogrammingrxjava2.utility.subjects.SubjectManager;
import com.larissa.reactiveprogrammingrxjava2.utility.subscribers.DemoSubscriber;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

// Example has been shaped so that it resembles the event-driven programming example from the video.
// Added another helper class called SubjectManager
public class PublishSubjectExample2 {

    private final static Logger log = LoggerFactory.getLogger(PublishSubjectExample2.class);

    // The names for our different subjects, so that our individual services can publish their events
    // to specific subject names.
    private final static String USER_SERVICE_SUBJECT_NAME = "UserServiceEventSubject";
    private final static String COMMENT_SERVICE_SUBJECT_NAME = "CommentServiceEventSubject";

    public static void main(String[] args) {

        // Create a SubjectManager
        SubjectManager subjectManager = new SubjectManager();

        // Register our two NamedSubjects with a SubjectManager.
        subjectManager.registerSubject(new NamedSubject(USER_SERVICE_SUBJECT_NAME, PublishSubject.create()));
        subjectManager.registerSubject(new NamedSubject(COMMENT_SERVICE_SUBJECT_NAME, PublishSubject.create()));

        // At least 1 event consumer needs to be present, else the Subject will detect
        // that no one is listening and dispose the producers.
        subjectManager.addEventConsumer(USER_SERVICE_SUBJECT_NAME, new DemoSubscriber());
        subjectManager.addEventConsumer(COMMENT_SERVICE_SUBJECT_NAME, new DemoSubscriber());

        // Now that we have the Consumer side of the equation set up
        // Let's set up some producers

        //Attach our message producers
        subjectManager.addEventProducer(
                USER_SERVICE_SUBJECT_NAME,
                UserServiceEventObservable.userServiceEventGenerator()
                        .subscribeOn(Schedulers.computation())
        );

        subjectManager.addEventProducer(
                COMMENT_SERVICE_SUBJECT_NAME,
                CommentServiceEventObservable.commentServiceEventGenerator()
                        .subscribeOn(Schedulers.computation())
        );


        ThreadHelper.sleep(10, TimeUnit.SECONDS);

        System.exit(0);
    }
}
