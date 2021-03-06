package example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.TreeWalk;
//import ecc.git.HistroyInfo;

public class JgitUtils {
	//D:\cfrManage\ConfigFile\设备配置文件管理\思科\192.168.0.248
	public JgitUtils() {
		
	}
	public static final String ID = "SiteView.ecc.editors.EccControl";
	static String URL="D:/cfrManage/ConfigFile";
	static String path="设备配置文件管理/思科/192.168.0.248";
	static Git git;
	public static void main(String[] args) {
		JgitUtils.getHistoryInfo(path,"192.168.0.248");
	}
	
	// 回滚到指定版本
	public static boolean reset(String gitRoot, String revision) {
		try {
			Git git = Git.open(new File(gitRoot));
			git.reset().setMode(ResetType.HARD).setRef(revision).call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

	// 读取历史记录
	public static void getHistoryInfo(String path, String serverAddress) {
		String[] configData = new String[8];
		git = null;
		File gitDir = new File(URL);
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
			//	TableItem tableItem = new TableItem(configTab, SWT.NONE);
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
				String tagName = getTag(version);

				configData[0] = name;
				configData[1] = time;
				configData[2] = author;
				configData[3] = mail;
				configData[4] = message;
				configData[5] = "标签：" + tagName;
				configData[6] = path + "/" + name;
				configData[7] = version;
				//tableItem.setText(configData);
			}
		} catch (Exception e) {
		}
	}


	public static void createFile(byte[] leftByte, byte[] rightByte) {
		FileOutputStream leftOut = null;
		FileOutputStream rightOut = null;
		File leftFile = new File("left.txt");
		File rigthFile = new File("right.txt");
		try {

			if (leftFile.exists()) {
				leftFile.delete();
				System.out.println("有新文件 ，已经删除");
			}
			if (rigthFile.exists()) {
				rigthFile.delete();
				System.out.println("有新文件 ，已经删除");
			}
			leftOut = new FileOutputStream("left.txt");
			leftOut.write(leftByte);
			rightOut = new FileOutputStream("right.txt");
			rightOut.write(rightByte);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				leftOut.flush();
				leftOut.close();

				rightOut.flush();
				rightOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 得到标签名 通过Tag objectID 得到revCommit ObjectID
	 */
	public static String getTag(String version) {
		String tagNameList = "";
		RevWalk walk = null;
		try {
			Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
			List<Ref> ref = git.tagList().call();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tagNameList;
	}

	/**
	 * 
	 * @param revision
	 *            版本号
	 * @param gitFilePath
	 *            文件路径
	 * @return
	 */
	public static byte[] getParentContent(String revision, String gitFilePath) {
		Git git;
		byte[] perContent = null;
		try {
			git = Git.open(new File(URL));
			Repository repository = git.getRepository();
			RevWalk walk = new RevWalk(repository);
			ObjectId objId = repository.resolve(revision);// 通过版本号分解，得到版本对象(String>>>object)
			RevCommit revCommit = walk.parseCommit(objId);
			String preVision = revCommit.getParent(0).getName();// 反回上一版本号
			ObjectId preId = repository.resolve(preVision);// 再次分解
			RevCommit revTree = walk.parseCommit(preId);// 反回树子树/文件的引用。
			RevTree tree = revTree.getTree();
			ObjectId objectId = tree.getId();
			TreeWalk treeWalk = TreeWalk.forPath(repository, gitFilePath,
					objectId);// 打开一棵树筛选一条路径。
			ObjectId treeObj = treeWalk.getObjectId(0);
			perContent = repository.open(treeObj).getBytes();// 得到文本数据
			repository.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return perContent;
	}

	// 获取文本内容
	public static byte[] read(String revision, String gitFilePath) {
		OutputStream out = null;
		Repository repository = null;
		Git git = null;
		try {
			if (git == null) {
				File gitDir = new File(URL);
				try {
					git = Git.open(gitDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			repository = git.getRepository();
			RevWalk walk = new RevWalk(repository);
			ObjectId objId = repository.resolve(revision);
			RevCommit revCommit = walk.parseCommit(objId);
			RevTree revTree = revCommit.getTree();
			TreeWalk treeWalk = TreeWalk.forPath(repository, gitFilePath,
					revTree);
			if (treeWalk == null)
				return null;
			ObjectId blobId = treeWalk.getObjectId(0);
			ObjectLoader loader = repository.open(blobId);
			byte[] bytes = loader.getBytes();
			if (bytes != null)
				return bytes;
		} catch (Exception e) {
		} finally {
			if (repository != null)
				repository.close();
		}
		return null;
	}
}
