package bootiful.crac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootApplication
public class CracApplication {

    public static void main(String[] args) {
        SpringApplication.run(CracApplication.class, args);
    }

}

@Component
class MyLifecycleBean implements SmartLifecycle {

    private final AtomicBoolean running = new AtomicBoolean(false);


    @Override
    public void start() {
        this.running.compareAndSet(false, true);
        System.out.println("starting...");
    }

    @Override
    public void stop() {
        this.running.compareAndSet(true, false);
        System.out.println("stopping");
    }

    @Override
    public boolean isRunning() {
        var running = this.running.get();
        System.out.println("is it running? " + running);
        return running;
    }


}
