package jgit.clone;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class JgitClone {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JgitClone.testClone();
		} catch (InvalidRemoteException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	public static void testClone() throws IOException, InvalidRemoteException,
			TransportException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File f = new File("D:/cfrManage/configfile");
		Repository repository = builder.setGitDir(f).readEnvironment()
				.findGitDir().build();
		Git git = new Git(repository);
		CloneCommand clone = git.cloneRepository();
		clone.setBare(false);
		clone.setCloneAllBranches(true);
		clone.setDirectory(f).setURI("http://127.0.0.1:8099/.git");
		UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
				"jonee", "123456");
		clone.setCredentialsProvider(user);
		// System.out.println("clone=" + clone);
		clone.call();

	}

}
