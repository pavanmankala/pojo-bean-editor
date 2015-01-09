package org.apache.pojo.beaneditor.model.outline;

public abstract class ContainerLeafNode {
    public static Object createLeafType(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            return new ContainerBooleanType();
        } else if (type == byte.class || type == Byte.class) {
            return new ContainerByteType();
        } else if (type == char.class || type == Character.class) {
            return new ContainerCharType();
        } else if (type == double.class || type == Double.class) {
            return new ContainerDoubleType();
        } else if (type == float.class || type == Float.class) {
            return new ContainerFloatType();
        } else if (type == int.class || type == Integer.class) {
            return new ContainerIntegerType();
        } else if (type == long.class || type == Long.class) {
            return new ContainerLongType();
        } else if (type == short.class || type == Short.class) {
            return new ContainerShortType();
        } else if (type == String.class) {
            return new ContainerStringType();
        }

        return null;
    }

    public static class ContainerBooleanType extends ContainerLeafNode {
        private Boolean boolValue;

        public Boolean getValue() {
            return boolValue;
        }

        public void setValue(Boolean boolValue) {
            this.boolValue = boolValue;
        }
    }

    public static class ContainerByteType extends ContainerLeafNode {
        private Byte byteValue;

        public Byte getValue() {
            return byteValue;
        }

        public void setValue(Byte boolValue) {
            this.byteValue = boolValue;
        }
    }

    public static class ContainerCharType extends ContainerLeafNode {
        private Character charValue;

        public Character getValue() {
            return charValue;
        }

        public void setValue(Character boolValue) {
            this.charValue = boolValue;
        }
    }

    public static class ContainerDoubleType extends ContainerLeafNode {
        private Double doubleValue;

        public Double getValue() {
            return doubleValue;
        }

        public void setValue(Double boolValue) {
            this.doubleValue = boolValue;
        }
    }

    public static class ContainerFloatType extends ContainerLeafNode {
        private Float floatValue;

        public Float getValue() {
            return floatValue;
        }

        public void setValue(Float boolValue) {
            this.floatValue = boolValue;
        }
    }

    public static class ContainerIntegerType extends ContainerLeafNode {
        private Integer integerValue;

        public Integer getValue() {
            return integerValue;
        }

        public void setValue(Integer boolValue) {
            this.integerValue = boolValue;
        }
    }

    public static class ContainerLongType extends ContainerLeafNode {
        private Long longValue;

        public Long getValue() {
            return longValue;
        }

        public void setValue(Long boolValue) {
            this.longValue = boolValue;
        }
    }

    public static class ContainerShortType extends ContainerLeafNode {
        private Short shortValue;

        public Short getValue() {
            return shortValue;
        }

        public void setValue(Short boolValue) {
            this.shortValue = boolValue;
        }
    }

    public static class ContainerStringType extends ContainerLeafNode {
        private String stringValue;

        public String getValue() {
            return stringValue;
        }

        public void setValue(String boolValue) {
            this.stringValue = boolValue;
        }
    }

}
