package com.jinanlongen.manatee.domain;

public class Category {
  private String code;
  private String name;
  private String pcode;
  private int level;
  private String path;



  public String getCode() {
    return code;
  }

  public Category setCode(String code) {
    this.code = code;
    return this;
  }

  public String getName() {
    return name;
  }

  public Category setName(String name) {
    this.name = name;
    return this;
  }

  public String getPcode() {
    return pcode;
  }

  public Category setPcode(String pcode) {
    this.pcode = pcode;
    return this;
  }



  public int getLevel() {
    return level;
  }

  public Category setLevel(int level) {
    this.level = level;
    return this;
  }

  public String getPath() {
    return path;
  }

  public Category setPath(String path) {
    this.path = path;
    return this;
  }

}
