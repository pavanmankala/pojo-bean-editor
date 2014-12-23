package org.apache.pojo.beaneditor.model.outline;
class StateInvariantError extends Error
{
    /**
     * Creates a new StateInvariantFailure object.
     *
     * @param s         a string indicating the assertion that failed
     */
    public StateInvariantError(String s) {
        super(s);
    }

}