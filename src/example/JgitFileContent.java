package example;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public class JgitFileContent {
	public static void main(String[] args) {
		getHistoryInfo();
	}

	static Git git;

	// ÎÄ¼þÄÚÈÝ
	public static void getHistoryInfo() {
		File gitDir = new File("D:/cfrManage/ConfigFile/.git");
		if (git == null) {
			try {
				git = Git.open(gitDir);
				Repository repo = git.getRepository();
				RevWalk revWalk = new RevWalk(repo);
				ObjectId objId = repo
						.resolve("f092789690370c7ce3ddfbd7b73ee402198cc60b");
				ObjectId treeId = revWalk.parseTree(objId);
				byte[] b = git.getRepository().open(treeId).getBytes();
				System.out.println(new String(b));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
