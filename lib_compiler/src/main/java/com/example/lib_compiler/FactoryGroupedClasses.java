package com.example.lib_compiler;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class FactoryGroupedClasses {

    private String mQualifiedClassName;

    /**
     * 将被添加到生成的工厂类的名字中
     */
    private static final String SUFFIX = "Factory";

    private Map<String, FactoryAnnotatedClass> mItemsMap =
            new LinkedHashMap<String, FactoryAnnotatedClass>();

    public FactoryGroupedClasses(String mQualifiedClassName) {
        this.mQualifiedClassName = mQualifiedClassName;
    }

    public void add(FactoryAnnotatedClass toInsert) throws IdAlreadyUsedException {

        FactoryAnnotatedClass existing = mItemsMap.get(toInsert.getId());
        if (existing != null) {
            throw new IdAlreadyUsedException(existing);
        }

        mItemsMap.put(toInsert.getId(), toInsert);
    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException {
        TypeElement superClassName = elementUtils.getTypeElement(mQualifiedClassName);
        String factoryClassName = superClassName.getSimpleName().toString() + SUFFIX;
        PackageElement pkg = elementUtils.getPackageOf(superClassName);
        //编写方法
        MethodSpec.Builder methodCreate = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(superClassName.asType()))
                .addParameter(String.class,"id");
        methodCreate.beginControlFlow("if (id == null)");
        methodCreate.addStatement("throw new IllegalArgumentException(\"id is null!\")");
        methodCreate.endControlFlow();
        for (FactoryAnnotatedClass value : mItemsMap.values()) {
            methodCreate.beginControlFlow("if ($S.equals(id))",value.getId());
            methodCreate.addStatement("return new $L()",value.getTypeElement().getQualifiedName());
            methodCreate.endControlFlow();
        }
        methodCreate.addStatement("throw new IllegalArgumentException(\"Unknown id = \" +id)");
        TypeSpec binder = TypeSpec.classBuilder(factoryClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(methodCreate.build())
                .build();
        JavaFile javaFile = JavaFile.builder(pkg.getQualifiedName().toString(),binder).build();
        javaFile.writeTo(filer);
    }
}
