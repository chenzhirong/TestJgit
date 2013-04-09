package example;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;

public class JgitGetVersion {
	static Git git;
	public static void main(String[] args) {
		getHistoryInfo();
	}
	//历史记录
	public static void getHistoryInfo() {
		File gitDir = new File("D:/cfrManage/ConfigFile/.git");
			try {
				if (git == null) {
					git = Git.open(gitDir);
				}
				Iterable<RevCommit> gitlog= git.log().call();
				for (RevCommit revCommit : gitlog) {
					String version=revCommit.getName();//版本号
					revCommit.getAuthorIdent().getName();
					revCommit.getAuthorIdent().getEmailAddress();
					revCommit.getAuthorIdent().getWhen();//时间
					System.out.println(version);
				}
			}catch (NoHeadException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
}
