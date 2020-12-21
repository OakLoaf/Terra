package com.dfsek.terra.api.structures.parser.lang;

import com.dfsek.terra.api.math.vector.Location;
import com.dfsek.terra.api.platform.world.Chunk;
import com.dfsek.terra.api.structures.tokenizer.Position;

public class ConstantExpression<T> implements Returnable<T> {
    private final T constant;
    private final Position position;

    public ConstantExpression(T constant, Position position) {
        this.constant = constant;
        this.position = position;
    }

    @Override
    public T apply(Location location) {
        return constant;
    }


    @Override
    public T apply(Location location, Chunk chunk) {
        return constant;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    public T getConstant() {
        return constant;
    }

    @Override
    public ReturnType returnType() {
        if(constant instanceof String) return ReturnType.STRING;
        if(constant instanceof Number) return ReturnType.NUMBER;
        if(constant instanceof Boolean) return ReturnType.BOOLEAN;
        return ReturnType.OBJECT;
    }
}
