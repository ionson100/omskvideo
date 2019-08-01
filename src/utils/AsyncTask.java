package utils;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTask<T1, T3> extends Task {


    //private static final Logger log = Logger.getLogger ( AsyncTaskKassa.class );

    private static final ExecutorService service = Executors.newFixedThreadPool(4);

    private T1[] params;

    protected abstract T3 doInBackground(T1... params);

    protected abstract void onPreExecute();

    protected abstract void onPostExecute(T3 params);

    protected abstract void onErrorInner(Throwable ex);

    private T3 res = null;

    @Override
    protected T3 call() {

        res = doInBackground(params);
        return res;
    }

    public void execute(T1[] params) {
        this.params = params;

        setOnScheduled((e) -> onPreExecute());

        this.setOnSucceeded((e) -> onPostExecute(res));

        this.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                onErrorInner(event.getSource().getException());

            }
        });


        //Executors.defaultThreadFactory().newThread(this).start();
        service.submit(this);


    }


}
