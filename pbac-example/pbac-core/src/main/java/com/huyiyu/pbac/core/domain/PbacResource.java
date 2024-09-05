package com.huyiyu.pbac.core.domain;

import java.io.Serializable;
import lombok.Data;

@Data
public class PbacResource implements Serializable {

  private Long id;
  private Long policyId;

}
