package com.rm.j2crm.domain.util.enums;

import java.util.Date;

public enum DateEnumUtil {
  GREATER_THAN {
    @Override
    public boolean comparedTo(Date target, Date another) {
      return target.compareTo(another) > 0;
    }
  },
  GREATER_EQUAL_THAN {
    @Override
    public boolean comparedTo(Date target, Date another) {
      return target.compareTo(another) >= 0;
    }
  },
  EQUAL {
    @Override
    public boolean comparedTo(Date target, Date another) {
      return target.compareTo(another) == 0;
    }
  },
  LESS_THAN {
    @Override
    public boolean comparedTo(Date target, Date another) {
      return target.compareTo(another) < 0;
    }
  },
  LESS_EQUAL_THAN {
    @Override
    public boolean comparedTo(Date target, Date another) {
      return target.compareTo(another) <= 0;
    }
  };

  public abstract boolean comparedTo(Date target, Date another);
}
