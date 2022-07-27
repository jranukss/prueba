package com.juan.prueba.git.controller;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.AbortedByHookException;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.ServiceUnavailableException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public class GitHelper {
    private static final String SSH_GIT_URI = "git@github.com:jranukss/reportsTest.git";
    private static final String LOCAL_REPOSITORY_PATH = "/Users/ff_465juan/Documents/prueba/test2";

    public static void main(String[] args)
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {

         
         cloneRemoteRepository();


         saveNewFile("multiTesting2.json", "contenido de lstReport.json");

         saveNewFile("another multiTesting2.json", "contenido de lstReport.json");

    }

    public static String logLocalRepository() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
        Git git = Git.open(new File(LOCAL_REPOSITORY_PATH));
        git.fetch().setTransportConfigCallback(new SshTransportConfigCallback()).call();

        Iterable<RevCommit> log = git.log().call();

        StringBuilder sb = new StringBuilder();

        for (RevCommit commit : log) {
            sb.append(commit.getName() + " " + commit.getAuthorIdent().getName() + "\n");

        }
        git.close();

        return sb.toString();

    }

    public static Boolean pullBranchRemote(String branch) throws IOException, WrongRepositoryStateException,
            InvalidConfigurationException, InvalidRemoteException, CanceledException, RefNotFoundException,
            RefNotAdvertisedException, NoHeadException, TransportException, GitAPIException {

        Git git = Git.open(new File(LOCAL_REPOSITORY_PATH + "/.git"));

        PullResult result = git.pull()
                .setTransportConfigCallback(new SshTransportConfigCallback())
                .setRemote("origin")
                .setRemoteBranchName(branch)
                .call();

        return result.isSuccessful();

    }

    public static String getBranchesRemoteRepository() {
        String result = "Hola mundo";
        try {

            StringBuilder sb = new StringBuilder();

            Collection<Ref> refs;
        
            refs = Git.lsRemoteRepository()
                    .setHeads(true)
                    .setRemote(SSH_GIT_URI)
                    .setTransportConfigCallback(new SshTransportConfigCallback())
                    .call();
            for (Ref ref : refs) {

                sb.append(ref.getName().substring(ref.getName().lastIndexOf("/") + 1, ref.getName().length()))
                        .append("\n");
            }

            result = sb.toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static void updateExistingFile(String filename, String content) throws IOException, AbortedByHookException,
            ConcurrentRefUpdateException, NoHeadException, NoMessageException, ServiceUnavailableException,
            UnmergedPathsException, WrongRepositoryStateException, GitAPIException, InvalidParameterException {
        Git git = Git.open(new File(LOCAL_REPOSITORY_PATH + "/.git"));

        String newFilePath = LOCAL_REPOSITORY_PATH + "/" + filename;

        FileWriter myWriter = new FileWriter(newFilePath);
        myWriter.write(content);
        myWriter.close();

        git.add().addFilepattern(".").call();
        git.add().setUpdate(true).addFilepattern(".").call();

        git.commit()
                .setMessage("Commit " + new Date().toString())
                .setAuthor("GIT SERVICE", "gitService@email.com")
                .call();

        PushCommand pushCommand = git.push();
        pushCommand.setRemote(SSH_GIT_URI);
        pushCommand.setTransportConfigCallback(new SshTransportConfigCallback());
        pushCommand.call();

        git.close();
    }

    public static void saveNewFile(String filename, String content) throws IOException, AbortedByHookException,
            ConcurrentRefUpdateException, NoHeadException, NoMessageException, ServiceUnavailableException,
            UnmergedPathsException, WrongRepositoryStateException, GitAPIException, InvalidParameterException {
        Git git = Git.open(new File(LOCAL_REPOSITORY_PATH + "/.git"));

        String newFilePath = LOCAL_REPOSITORY_PATH + "/" + filename;

        File newFile = new File(newFilePath);
        if (!newFile.createNewFile()) {
            throw new InvalidParameterException("File already exists");
        }

        FileWriter myWriter = new FileWriter(newFilePath);
        myWriter.write(content);
        myWriter.close();

        git.add().addFilepattern(".").call();
        git.add().setUpdate(true).addFilepattern(".").call();
        LocalDate date = LocalDate.now();

        git.commit()
                .setMessage("Commit " + date.toString())
                .setAuthor("GIT SERVICE", "gitService@email.com")
                .call();

        PushCommand pushCommand = git.push();
        pushCommand.setRemote(SSH_GIT_URI);
        pushCommand.setTransportConfigCallback(new SshTransportConfigCallback());
        pushCommand.call();

        git.close();
    }

    public static void cloneRemoteRepository()
            throws InvalidRemoteException, TransportException, GitAPIException, IOException {
        File workingDir = new File(LOCAL_REPOSITORY_PATH);
        TransportConfigCallback transportConfigCallback = new SshTransportConfigCallback();
        Git git = Git.cloneRepository().setDirectory(workingDir).setTransportConfigCallback(
                transportConfigCallback)
                .setURI(SSH_GIT_URI).call();
        git.close();
    }

    private static class SshTransportConfigCallback implements TransportConfigCallback {
      

        @Override
        public void configure(Transport transport) {
            SshTransport sshTransport = (SshTransport) transport;

            SshSessionFactory sessionFactory = SshSessionFactory.getInstance();
            
            sshTransport.setSshSessionFactory(sessionFactory);

          
        }
    }


        

}