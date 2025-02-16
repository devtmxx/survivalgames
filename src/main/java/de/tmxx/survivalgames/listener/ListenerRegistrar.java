package de.tmxx.survivalgames.listener;

import java.lang.annotation.Annotation;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface ListenerRegistrar {
    void registerGeneral();
    void registerPhaseSpecific(Class<? extends Annotation> annotation);
    void unregisterPhaseSpecific(Class<? extends Annotation> annotation);
}
