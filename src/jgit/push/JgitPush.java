package jgit.push;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import tftp.server.TftpServerStart;

public class JgitPush {
	private static String localPath = "D:/cfrManage/ConfigFile";
	private static String remotePath = "http://127.0.0.1:8099/.git";
	private static String UserName = "jonee";
	private static String UserPassword = "123456";
	private static Git git;

	public static void getGit() {
		try {
			Repository localRepo = new FileRepository(localPath + "/.git");
			git = new Git(localRepo);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 * @throws GitAPIException
	 * @throws IOException
	 * @throws TransportException
	 * @throws InvalidRemoteException
	 */
	public static void main(String[] args) throws InvalidRemoteException,
			TransportException, IOException, GitAPIException {
		JgitPush.getGit();// 初始化GIT
		try {
			// 提交文件到本地
			// commitConifgFile(new File("D:/cfrManage/ConfigFile/设备配置文件管理/思科/192.168.0.248/192.168.0.248confit.txt"));
			// testpush();//提交到远程
			//testClone();//从远程克隆下来
			//boolean flg = createTag("ce2110fa2cf47c60868e1b0284c62968892e6d20",
					//"rongzai1", "rongzai521");
			//System.out.println(flg);
			//获取指定版本标签
			System.out.println(getTag("c25fcfe1f1bf6d75c0a57232bab141c81a49b5c9"));
			//删除标签
			//deleteTag("rongzai");
			 //testpull();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// createTag("ce2110fa2cf47c60868e1b0284c62968892e6d20","v1.6","version 1.6");
	}
	/**
	 * 删除标签
	 */
	public static boolean deleteTag(String tagName) {
		//Git git = Git.open(new File(gitPath));
		try {
			git.tagDelete().setTags(tagName).call();
		} catch (ConcurrentRefUpdateException e) {
		} catch (InvalidTagNameException e) {
			e.printStackTrace();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void init() {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File f = new File(localPath);
		Repository repository;
		try {
			repository = builder.setGitDir(f).readEnvironment().findGitDir()
					.build();
			// git = new Git(repository);
			// git = Git.open(new File(localPath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * clone 库 到本地
	 * 
	 * @throws IOException
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public static void testClone() throws IOException, InvalidRemoteException,
			TransportException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File f = new File(localPath);
		Repository repository = builder.setGitDir(f).readEnvironment()
				.findGitDir().build();
		Git git = new Git(repository);
		CloneCommand clone = git.cloneRepository();
		clone.setBare(false);
		clone.setCloneAllBranches(true);
		clone.setDirectory(f).setURI(remotePath);
		UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
				UserName, UserPassword);
		clone.setCredentialsProvider(user);
		clone.call();
	}

	/**
	 * 将配置文件存到一个git库中
	 */

	public static void commitConifgFile(File cfile) throws IOException,
			NoFilepatternException, GitAPIException {
		if (!cfile.isFile()) {
			cfile.createNewFile();
		}
		git.add().addFilepattern(".").call();
		RevCommit commit = git.commit().setMessage("文件有更新").call();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 提交tag到一个git库中
	 */

	public static void commitTag() throws IOException, NoFilepatternException,
			GitAPIException {
		git.add().addFilepattern(".").call();
		RevCommit commit = git.commit().setMessage("").call();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传到服务器
	 * 
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 * @throws Exception
	 */
	public static void testpush() throws InvalidRemoteException,
			TransportException, GitAPIException, Exception {
		PushCommand push = git.push();
		//push.setPushTags();
		push.setPushAll();
		push.setRemote(remotePath);
		UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
				UserName, UserPassword);
		push.setCredentialsProvider(user);
		push.call();
	}

	/**
	 * pull到本地
	 * 
	 * @throws GitAPIException
	 * @throws TransportException
	 * @throws NoHeadException
	 * @throws RefNotFoundException
	 * @throws CanceledException
	 * @throws InvalidRemoteException
	 * @throws DetachedHeadException
	 * @throws InvalidConfigurationException
	 * @throws Exception
	 */
	public static void testpull() throws Exception {
		PullCommand pull = git.pull();
		UsernamePasswordCredentialsProvider user = new UsernamePasswordCredentialsProvider(
				UserName, UserPassword);
		pull.setCredentialsProvider(user);
		pull.call();
	}

	/**
	 * 对指定版本打标签
	 */
	public static boolean createTag(String version, String tagName,
			String message) {
		try {
			RevWalk walk = new RevWalk(git.getRepository());
			ObjectId obje = git.getRepository().resolve(version);
			RevCommit commit = walk.parseCommit(obje);
			try {
				git.tag().setObjectId(commit).setName(tagName)
						.setMessage(message).call();
				// testpush();
			} catch (ConcurrentRefUpdateException e) {
			} catch (InvalidTagNameException e) {
				e.printStackTrace();
			} catch (NoHeadException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
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


}
