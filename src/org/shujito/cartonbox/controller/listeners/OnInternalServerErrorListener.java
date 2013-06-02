package org.shujito.cartonbox.controller.listeners;

import org.shujito.cartonbox.model.parser.JsonParser;

public interface OnInternalServerErrorListener
{ public void onInternalServerError(JsonParser<?> jarr); }