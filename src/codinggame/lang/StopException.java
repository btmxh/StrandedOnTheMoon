/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codinggame.lang;

/**
 *
 * @author Welcome
 */
public class StopException extends RuntimeException {

    /**
     * Creates a new instance of <code>StopException</code> without detail
     * message.
     */
    public StopException() {
        this("Thread Stopped");
    }

    /**
     * Constructs an instance of <code>StopException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public StopException(String msg) {
        super(msg);
    }
}
