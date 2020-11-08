package com.yifeng.bank.b.service;

public interface RegisterService {

    String registerResourceManager(String serviceName);

    String registerBranchTransaction(String XID);

    boolean rollback(String XID, String branchId);

    boolean deleteUndoLog(String XID, String branchId);
}
