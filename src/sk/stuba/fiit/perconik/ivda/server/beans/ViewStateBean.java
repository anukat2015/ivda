package sk.stuba.fiit.perconik.ivda.server.beans;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by Seky on 20. 8. 2014.
 * <p>
 * V nasom webe sa mozu nachadzat rozne stranky. Kazdu stranku definujeme ako stav.
 * Tieto stavy mozme prepinat.
 */
@ManagedBean(name = "viewState")
@ViewScoped
public class ViewStateBean implements Serializable {
    private static final long serialVersionUID = -6661844962434667667L;
    private ViewState state = ViewState.EVENT;

    public ViewState getState() {
        return state;
    }

    public void setState(ViewState state) {
        this.state = state;
    }

    public String getViewName() {
        return this.state.getViewName();
    }

    public enum ViewState implements Serializable {
        EVENT("event.xhtml"),
        CHANGED_FILES("changedFiles.xhtml"),
        DIFF_FILES("diffFiles.xhtml");

        String viewName;

        private ViewState(String viewName) {
            this.viewName = viewName;
        }

        public String getViewName() {
            return viewName;
        }
    }
}

