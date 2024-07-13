package com.amazonas.backend.repository;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class ShortNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalColumnName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSequenceName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier identifier, final JdbcEnvironment jdbcEnv) {
        return convertToSnakeCase(identifier);
    }

    private Identifier convertToSnakeCase(final Identifier identifier) {
        if(identifier == null) {
            return null;
        }
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
          .replaceAll(regex, replacement)
          .toLowerCase()
                .replaceAll("user","usr")
                .replaceAll("store","str")
                .replaceAll("shipping","shipng")
                .replaceAll("payment","pymnt")
                .replaceAll("notification","ntfctn")
                .replaceAll("permission","prmsn")
                .replaceAll("authentication","auth")
                .replaceAll("product","prdct")
                .replaceAll("transaction","trnsctn")
                .replaceAll("profile","prfl")
                .replaceAll("rating","rtng")
                .replaceAll("inventory","inv")
                .replaceAll("password","pswd")
                .replaceAll("disabled","dsbld")
                .replaceAll("order","ordr")
                .replaceAll("quantity","qty")
                .replaceAll("appointment","appt")
                .replaceAll("system","sys")
                .replaceAll("open","opn")
                .replaceAll("description","dsc")
                .replaceAll("manager","mngr")
                .replaceAll("list","lst")
                .replaceAll("ownership","ownrshp")
                .replaceAll("children","chldrn")
                .replaceAll("owner","ownr")
                .replaceAll("position","pstn")
                .replaceAll("registered","rgstrd")
                .replaceAll("birth","brth")
                .replaceAll("date","dte")
                .replaceAll("email","eml")
                .replaceAll("shopping","shopng")
                .replaceAll("basket","bskt")
                .replaceAll("cart","crt")
                .replaceAll("reserved","rsrvd")
                .replaceAll("message","msg")
                .replaceAll("timestamp","tmstmp")
                .replaceAll("sender","sndr")
                .replaceAll("receiver","rcvr")
                .replaceAll("category","ctgry")
                .replaceAll("title","ttl")
                .replaceAll("word","wrd")
                .replaceAll("price","prce")
                .replaceAll("name","nm")
                .replaceAll("state","stt")
                .replaceAll("action","act")
                .replaceAll("allowed","alwd")
                .replaceAll("market","mrkt")
                .replaceAll("node","nd")
                .replaceAll("collection","cllctn")
                .replaceAll("credentials","crdntls");
        return Identifier.toIdentifier(newName);
    }
}