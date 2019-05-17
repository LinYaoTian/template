package com.example.lib_compiler;

import com.example.lib_annotations.annotation.Factory;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class FactoryProcess extends AbstractProcessor {

    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    private Map<String, FactoryGroupedClasses> mFactoryClasses =
            new LinkedHashMap<String, FactoryGroupedClasses>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
        mElementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(Factory.class)) {
                // 检查被注解了 @Factory 的元素类型是否为一个类
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    error(annotatedElement,
                            "Only Classes can be annotated with @%s",
                            Factory.class.getSimpleName());
                    return true;
                }
                TypeElement typeElement = (TypeElement) annotatedElement;

                FactoryAnnotatedClass factoryAnnotatedClass = new FactoryAnnotatedClass(typeElement);
                if (!isValidClass(factoryAnnotatedClass)) {
                    return true;
                }
                // 所有检查没有问题，可以添加了
                FactoryGroupedClasses factoryGroupedClasses =
                        mFactoryClasses.get(factoryAnnotatedClass.getQualifiedFactoryGroupName());
                if (factoryGroupedClasses == null) {
                    String qualifiedGroupName = factoryAnnotatedClass.getQualifiedFactoryGroupName();
                    factoryGroupedClasses = new FactoryGroupedClasses(qualifiedGroupName);
                    mFactoryClasses.put(qualifiedGroupName, factoryGroupedClasses);
                }
                factoryGroupedClasses.add(factoryAnnotatedClass);
            }
            // Generate code
            for (FactoryGroupedClasses factoryClass : mFactoryClasses.values()) {
                factoryClass.generateCode(mElementUtils, mFiler);
            }
            mFactoryClasses.clear();
        } catch (IllegalArgumentException e) {
            error(null, e.getMessage());
            return true;
        } catch (IdAlreadyUsedException e) {
            FactoryAnnotatedClass existing = e.getExisting();
            error(existing.getTypeElement(),
                    "Conflict: The class %s is annotated with @%s with id ='%s' but %s already uses the same id",
                    existing.getSimpleFactoryGroupName(),
                    Factory.class.getSimpleName(),
                    existing.getTypeElement().getQualifiedName().toString());
        } catch (IOException e) {
            e.printStackTrace();
            error(null, e.getMessage());
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(Factory.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private boolean isValidClass(FactoryAnnotatedClass item)  {
        // 转换为TypeElement, 含有更多特定的方法
        TypeElement classElement = item.getTypeElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            error(classElement, "The class %s is not public.",
                    classElement.getQualifiedName().toString());
            return false;
        }

        // 检查是否是一个抽象类
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            error(classElement, "The class %s is abstract. You can't annotate abstract classes with @%",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
            return false;
        }

        // 检查继承关系: 必须是@Factory.type()指定的类型子类
        TypeElement superClassElement =
                mElementUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (superClassElement.getKind() == ElementKind.INTERFACE) {
            // 检查接口是否实现了
            // Check interface implemented
            if (!classElement.getInterfaces().contains(superClassElement.asType())) {
                error(classElement,
                        "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        item.getQualifiedFactoryGroupName());
                return false;
            }
        } else {
            // 检查子类
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();
                if (superClassType.getKind() == TypeKind.NONE) {
                    // 到达了基本类型(java.lang.Object), 所以退出
                    error(classElement, "The class %s annotated with @%s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                            item.getQualifiedFactoryGroupName());
                    return false;
                }

                if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())) {
                    // 找到了要求的父类
                    break;
                }

                // 在继承树上继续向上搜寻
                currentClass = (TypeElement) mTypeUtils.asElement(superClassType);
            }
        }

        // 检查是否提供了默认公开构造函数
        for (Element enclosed : classElement.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers()
                        .contains(Modifier.PUBLIC)) {
                    // 找到了默认构造函数
                    return true;
                }
            }
        }

        // 没有找到默认构造函数
        error(classElement, "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
        return false;
    }


    private void error(Element e, String s, String... args) {
        mMessager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(s, (Object[]) args),
                e);
    }

}
