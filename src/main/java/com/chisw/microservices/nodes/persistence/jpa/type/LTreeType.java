package com.chisw.microservices.nodes.persistence.jpa.type;


import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;


import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * This class is needed for binding String java type to ltree (@link https://www.postgresql.org/docs/9.1/static/ltree.html) type
 */
public class LTreeType implements UserType {

    @Override
    public int[] sqlTypes() {
        return  new int[] {Types.OTHER};
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class returnedClass() {
        return String.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(
            ResultSet rs, String[] names, SessionImplementor session, Object owner
    ) throws HibernateException, SQLException {

        return rs.getString(names[0]);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        st.setObject(index, value, Types.OTHER);
    }


    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return (String) value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable)value;
    }

    @Override
    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        return deepCopy(original);
    }


    public static void main(String[] args) {
        System.out.println("select node0_.id as id1_0_, node0_.path as path2_0_ from nodes node0_ where node0_.path ~ ?::lquery=true".substring(57, 69));
    }

}