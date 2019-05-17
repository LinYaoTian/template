package com.example.lib_compiler;

class IdAlreadyUsedException extends Exception {

    private FactoryAnnotatedClass mExisting;

    public IdAlreadyUsedException(FactoryAnnotatedClass factoryAnnotatedClass){
        mExisting = factoryAnnotatedClass;
    }

    public FactoryAnnotatedClass getExisting() {
        return mExisting;
    }
}
