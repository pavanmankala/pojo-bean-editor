package org.apache.pojo.beaneditor;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pojo.beaneditor.model.outline.PBEOAggregatedNode;
import org.apache.pojo.beaneditor.model.outline.PBEOLeafNode;
import org.apache.pojo.beaneditor.model.outline.PBEONodeTyp_Array;
import org.apache.pojo.beaneditor.model.outline.PBEONodeTyp_Collection;
import org.apache.pojo.beaneditor.model.outline.PBEONodeTyp_Map;
import org.apache.pojo.beaneditor.model.outline.PBEUtils;

public class PBEBeanParser {
    public static PBEOAggregatedNode parseBean(PojoBeanCreator creator, Object obj) {
        PBEOAggregatedNode root = new PBEOAggregatedNode("root", null, null);

        parseBean0(creator, obj.getClass(), root, obj);
        return root;
    }

    private static void parseBean0(PojoBeanCreator creator, Class<?> clazz, PBEOAggregatedNode parentNode,
            Object parentObj) {
        Map<String, PBENodeValueMutator> mutators = new HashMap<String, PBENodeValueMutator>();
        List<Method> declaredMethods = new ArrayList<Method>(Arrays.asList(clazz.getDeclaredMethods()));
        Class<?> parentClazz = clazz.getSuperclass();

        while (parentClazz != Object.class) {
            declaredMethods.addAll(Arrays.asList(parentClazz.getDeclaredMethods()));
            parentClazz = parentClazz.getSuperclass();
        }

        Collections.sort(declaredMethods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (Method m : declaredMethods) {
            final int modifier = m.getModifiers();

            if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                continue;
            }

            String mName = m.getName();
            boolean isGet = true;

            if (mName.startsWith("get")) {
                mName = mName.substring(3);
            } else if (mName.startsWith("set")) {
                mName = mName.substring(3);
                isGet = false;
            } else {
                continue;
            }

            if (mutators.containsKey(mName) || mName.equals("Class")) {
                continue;
            }

            PBENodeValueMutator mutator;
            Class<?> paramTyp = isGet ? m.getReturnType() : m.getParameterTypes()[0];

            try {
                mutator = new PBENodeValueMutator(
                        isGet ? clazz.getMethod("set" + mName, new Class[] { paramTyp, }) : m, isGet ? m
                                : clazz.getMethod("get" + mName, new Class[] {}));
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(e);
            }

            mutators.put(mName, mutator);

            if (PBEUtils.isRepresentedAsLeaf(paramTyp)) {
                parentNode.addElement(new PBEOLeafNode(mName, mutator, parentObj));
            } else if (Map.class.isAssignableFrom(paramTyp)) {
                parentNode.addElement(new PBEONodeTyp_Map(mName, creator, mutator, parentObj));
            } else if (Collection.class.isAssignableFrom(paramTyp)) {
                parentNode.addElement(new PBEONodeTyp_Collection(mName, creator, mutator, parentObj));
            } else if (Array.class.isAssignableFrom(paramTyp)) {
                parentNode.addElement(new PBEONodeTyp_Array(mName, creator, mutator, parentObj));
            } else {
                Object elemValue;

                try {
                    elemValue = mutator.getElemGetter().invoke(parentObj, new Object[0]);

                    if (elemValue == null) {
                        elemValue = creator.createPojoBean(paramTyp);
                        mutator.getElemSetter().invoke(parentObj, new Object[] { elemValue });
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }

                PBEOAggregatedNode newNode = new PBEOAggregatedNode(mName, mutator, parentObj);
                parentNode.addElement(newNode);

                parseBean0(creator, paramTyp, newNode, elemValue);
            }
        }
    }
}
