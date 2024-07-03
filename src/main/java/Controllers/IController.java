package Controllers;

import Database.ObjectClasses.Worker;

/**
 * Interface that allows a Worker to be passed to controller classes to check permissions of the worker for
 * various functions
 */
public interface IController {

    void initWorker(Worker worker);

}