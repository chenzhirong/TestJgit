package SiteView.ecc.jgit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;

public class JgitTag {
	public static void main(String[] args) {
		JgitTag.getHistoryInfo("设备配置文件管理/思科/192.168.0.248","192.168.0.248");
	}
	// 读取历史记录
	public static void getHistoryInfo(String path, String serverAddress) {
		String[] configData = new String[8];
		Git git = null;
		File gitDir = new File("D:/cfrManage/ConfigFile/.git");
		Repository repository;
		try {
			if (git == null) {
				git = Git.open(gitDir);
			}
			repository = git.getRepository();
			RevWalk walk = new RevWalk(repository);
			// jonee测试 child
			Map<String, Ref> m = repository.getAllRefs();
			Collection<Ref> c = m.values();
			Iterator it = c.iterator();
			for (; it.hasNext();) {
				System.out.println(it.next());
			}
			ListBranchCommand listb = git.branchList();
			Iterable<RevCommit> gitlog = git.log().addPath(path).call();

			for (RevCommit revCommit : gitlog) {
				String name = serverAddress + "config.txt";
				String author = revCommit.getAuthorIdent().getName();
				String mail = revCommit.getAuthorIdent().getEmailAddress();
				Date date = revCommit.getAuthorIdent().getWhen();
			   // String time=String.valueOf(date);
				SimpleDateFormat simple = new SimpleDateFormat(
						"yyyy年MM月dd日hh:mm:ss");
				String time = simple.format(date);
				String version = revCommit.getName();
				String message = revCommit.getFullMessage();
				// 获取标签
				 getTag(version);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void getTag(String version) {
		String tagNameList = "";
		RevWalk walk = null;
		try {
			Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
			List<Ref> ref=git.tagList().call();
			for (int i = 0; i < ref.size(); i++) {
				Ref gitRef = ref.get(i);
				ObjectId refId = gitRef.getObjectId();
				walk = new RevWalk(git.getRepository());
				RevObject revObject = walk.parseAny(refId);
				if (revObject instanceof RevCommit) {

				} else if (revObject instanceof RevTag) {
					RevCommit revCommit = walk.parseCommit(revObject);
					if (revCommit.getName().equals(version)) {
						tagNameList = ((RevTag) revObject).getTagName() + "/"
								+ tagNameList;

					}

				} else if (revObject instanceof RevTree) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
}
