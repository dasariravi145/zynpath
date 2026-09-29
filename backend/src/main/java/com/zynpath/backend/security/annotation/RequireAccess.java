package com.zynpath.backend.security.annotation;

import com.zynpath.backend.security.model.EndpointAccessTier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the mandatory access tier required to invoke a REST controller class or handler method.
 *
 * Implements Prompt 36 Section 7 & 8:
 * - Handlers without this annotation inherit DENY-BY-DEFAULT policy unless explicitly configured in public routes.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireAccess {
    EndpointAccessTier value() default EndpointAccessTier.AUTHENTICATED;
}
