package de.tmxx.survivalgames.auto;

import de.tmxx.survivalgames.game.GameState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     This annotation is necessary for brigadier commands and listeners that should automatically be registered by the
 *     {@link AutoRegistrar}. It is used to tell the registrar when to register the specific command / event listener.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRegister {
    /**
     * The {@link RegisterState} is used to decide if the underlying entity should be registered for setup, the game or
     * always.
     *
     * @return the register state
     */
    RegisterState value();

    /**
     * The game states the underlying entity should be registered for. This will be ignored if the {@link RegisterState}
     * is {@link RegisterState#ALWAYS} oder {@link RegisterState#SETUP} as game states only exist during the game. If
     * the register state is {@link RegisterState#GAME} at least one game state should be specified or the underlying
     * entity won't be registered at all.
     *
     * @return the game states
     */
    GameState[] states() default {};
}
