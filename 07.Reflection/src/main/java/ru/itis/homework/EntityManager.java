package ru.itis.homework;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EntityManager {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public EntityManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // createTable("account", User.class);x
    public <T> void createTable(String tableName, Class<T> entityClass) {
        // сгенерировать CREATE TABLE на основе класса
        // create table account ( id integer, firstName varchar(255), ...))
        try {
            StringBuilder createdTable = new StringBuilder();
            createdTable.append("create table ").append(tableName).append("( ");

            Class<?> aClass = Class.forName(entityClass.getName());
            Field[] fields = aClass.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                String type;

                switch (fields[i].getType().getSimpleName()) {
                    case "Long":
                        type = "bigint";
                        break;
                    case "String":
                        type = "VARCHAR(255)";
                        break;
                    default:
                        type = fields[i].getType().getSimpleName();
                        break;
                }

                if (i != fields.length - 1) {
                    createdTable.append(fields[i].getName().toLowerCase()).append(" ").append(type).append(", ");
                } else {
                    createdTable.append(fields[i].getName().toLowerCase()).append(" ").append(type).append(" ");
                }
            }

            createdTable.append(");");
            System.out.println(createdTable);
            jdbcTemplate.execute(String.valueOf(createdTable));
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void save(String tableName, Object entity) {
        try {
            Class<?> classOfEntity = entity.getClass();

            StringBuilder saveValues = new StringBuilder();
            saveValues.append("INSERT INTO ").append(tableName).append(" (");
            StringBuilder values = new StringBuilder();

            Field[] fields = classOfEntity.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (i != fields.length - 1) {
                    saveValues.append(fields[i].getName().toLowerCase()).append(", ");
                    for (Method method : classOfEntity.getDeclaredMethods()) {
                        if (method.getReturnType().getName().equals(fields[i].getType().getName())) {
                            if (method.getName().toLowerCase().contains(fields[i].getName().toLowerCase())) {
                                Object result = method.invoke(entity);
                                if (method.getReturnType().getSimpleName().equals("String")) {
                                    values.append("'").append(result).append("'");
                                } else {
                                    values.append(result);
                                }
                                values.append(", ");
                            }
                        }
                    }
                } else {
                    saveValues.append(fields[i].getName().toLowerCase());
                    for (Method method : classOfEntity.getDeclaredMethods()) {
                        if (method.getName().toLowerCase().contains(fields[i].getName().toLowerCase())) {
                            Object result = method.invoke(entity);
                            if (method.getReturnType().getSimpleName().equals("String")) {
                                values.append("'").append(result).append("'");
                            } else {
                                values.append(result);
                            }
                        }
                    }
                }
            }
            saveValues.append(") ").append("values").append(" (");
            saveValues.append(values.toString());
            saveValues.append(");");

            System.out.println(saveValues);
            jdbcTemplate.execute(String.valueOf(saveValues));

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public <T, ID extends Number> T findById(String tableName, Class<T> resultType, Class<ID> idType, ID idValue) {
        String sql = String.format("select * from %s where id = %s", tableName, idValue);

        Field[] f = resultType.getDeclaredFields();
        Class<?>[] types = new Class<?>[f.length];

        for (int i = 0; i < f.length; i++) {
            types[i] = f[i].getType();
        }

        Object[] variables = new Object[f.length];

        try {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                Field[] fields = resultType.getDeclaredFields();
                if (resultSet.next()) {
                    for (int i = 0; i < fields.length; i++) {
                        variables[i] = resultSet.getObject(i + 1);
                    }
                }
                System.out.println(Arrays.toString(variables));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        try {
            Constructor<?> constructor = resultType.getConstructor(types);
            Object object = constructor.newInstance(variables);
            return (T) object;
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}