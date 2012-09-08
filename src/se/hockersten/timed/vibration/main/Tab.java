package se.hockersten.timed.vibration.main;

public interface Tab {
    /** 
     * Called when this Tab becomes the selected tab.
     * This is *not* called when for example the application loses focus. 
     */
    public void onTabVisible();
    /** 
     * Called when this Tab stops being the selected tab.
     * This is *not* called when for example the application loses focus. 
     */
    public void onTabInvisible();
}
