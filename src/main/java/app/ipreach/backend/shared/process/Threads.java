package app.ipreach.backend.shared.process;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Threads {

    public static void runInBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

}
