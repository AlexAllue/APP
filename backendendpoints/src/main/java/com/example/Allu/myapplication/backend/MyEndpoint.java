/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.example.Allu.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.ArrayList;
import java.util.List;


/**
 * An endpoint class we are exposing
 */
@Api(name = "taskUser", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.myapplication.Allu.example.com", ownerName = "backend.myapplication.Allu.example.com", packagePath = ""))
public class MyEndpoint {
    @ApiMethod(name = "storeTask")
    public void storeTask(TaskBean taskBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("TaskBeanParent", "todo.txt");
            Entity taskEntity = new Entity("TaskBean", taskBean.getId(), taskBeanParentKey);
            taskEntity.setProperty("user", taskBean.getUser());
            taskEntity.setProperty("password", taskBean.getPassword());
            taskEntity.setProperty("email", taskBean.getEmail());

            datastoreService.put(taskEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @ApiMethod(name = "getTasks")
    public List<TaskBean> getTasks() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key taskBeanParentKey = KeyFactory.createKey("TaskBeanParent", "todo.txt");
        Query query = new Query(taskBeanParentKey);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<TaskBean> taskBeans = new ArrayList<TaskBean>();
        for (Entity result : results) {
            TaskBean taskBean = new TaskBean();
            taskBean.setId(result.getKey().getId());
            taskBean.setUser((String) result.getProperty("user"));
            taskBean.setPassword((String) result.getProperty("password"));
            taskBean.setEmail((String) result.getProperty("email"));
            taskBeans.add(taskBean);
        }

        return taskBeans;
    }

    @ApiMethod(name = "clearTasks")
    public void clearTasks() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("TaskBeanParent", "todo.txt");
            Query query = new Query(taskBeanParentKey);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            for (Entity result : results) {
                datastoreService.delete(result.getKey());
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @ApiMethod(name = "storeTaskData")
    public void storeTaskData(TaskData taskBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("TaskDataParent", "todo.txt");
            Entity taskEntity = new Entity("TaskData", taskBean.getId(), taskBeanParentKey);
            taskEntity.setProperty("town", taskBean.getTown());
            taskEntity.setProperty("degrees", taskBean.getDegrees());
            taskEntity.setProperty("humidity", taskBean.getHumidity());
            taskEntity.setProperty("longitude", taskBean.getLongitude());
            taskEntity.setProperty("latitude", taskBean.getLatitude());
            taskEntity.setProperty("optional", taskBean.getOpcional());
            datastoreService.put(taskEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    @ApiMethod(name = "getTasksData")
    public List<TaskData> getTasksData() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key taskBeanParentKey = KeyFactory.createKey("TaskDataParent", "todo.txt");
        Query query = new Query(taskBeanParentKey);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
        ArrayList<TaskData> taskDatas = new ArrayList<TaskData>();
        for (Entity result : results) {
            TaskData taskBean = new TaskData();
            taskBean.setId(result.getKey().getId());
            taskBean.setTown((String) result.getProperty("town"));
            taskBean.setDegrees((String) result.getProperty("degrees"));
            taskBean.setHumidity((String) result.getProperty("humidity"));
            taskBean.setLongitude((String) result.getProperty("longitude"));
            taskBean.setLatitude((String) result.getProperty("latitude"));
            taskBean.setOpcional((String) result.getProperty("optional"));
            taskDatas.add(taskBean);
        }

        return taskDatas;
    }

    @ApiMethod(name = "clearTasksData")
    public void clearTasksData() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("TaskDataParent", "todo.txt");
            Query query = new Query(taskBeanParentKey);
            List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
            for (Entity result : results) {
                datastoreService.delete(result.getKey());
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

}
