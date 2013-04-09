package example;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.TagBuilder;
import org.eclipse.jgit.revwalk.FooterKey;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.WalkTransport;
import org.eclipse.jgit.treewalk.TreeWalk;

public class Snippet {
	static Repository  repository;
	
	public static String getContentWithFile(String gitRoot,
			final String branchName, String fileName) throws Exception {
		final Git git = Git.open(new File(gitRoot));
		repository = git.getRepository();
		 Map<String, Ref> refMap=repository.getAllRefs();
		 System.out.println(repository.getBranch());
		 System.out.println(repository.getFullBranch());
		RevWalk walk = new RevWalk(repository);
		Ref ref = repository.getRef(repository.getBranch());
		if (ref == null) {
			// 获取远程分支
			ref = repository.getRef(branchName);
		}
		// 异步pull
		ExecutorService executor = Executors.newCachedThreadPool();
		ObjectId objId = ref.getObjectId();
		System.out.println(objId);
		
	    RevCommit revCommit = walk.parseCommit(objId);
		System.out.println(revCommit.getAuthorIdent().getEmailAddress());
	    System.out.println(revCommit.getAuthorIdent().getName());
	    System.out.println(revCommit.getName());
		
		RevTag revTag =walk.parseTag(objId);
		System.out.println(revTag.getTaggerIdent().getName());
		System.out.println(revTag.getFullMessage());
		
		RevTree revTree = revCommit.getTree();
		RevObject revObject=(RevObject) revTree.getId();
		
		TreeWalk treeWalk = TreeWalk.forPath(repository, fileName, revTree);
		// 文件名错误
		if (treeWalk == null)
			return null;
		ObjectId blobId = treeWalk.getObjectId(0);
		ObjectLoader loader = repository.open(blobId);
		byte[] bytes = loader.getBytes();
		if (bytes != null)
			return new String(bytes);
		return null;
	}
	public static void main(String[] args) {
		try {
			Snippet.getContentWithFile("D:/cfrManage/ConfigFile/.git","","设备配置文件管理/思科/192.168.0.248");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void getHistory(){
		File gitDir=new File("D:/cfrManage/ConfigFile/.git");
		try {
			Git git=Git.open(gitDir);
			repository=git.getRepository();
            RevWalk walk=new RevWalk(repository);
			Iterable<RevCommit> revcommit=git.log().addPath("设备配置文件管理/思科/192.168.0.248").call();
			List<Ref> listTag=git.tagList().call();
			for (Ref ref : listTag) {
			System.out.println("refs/tags/all=="+ref.getName());
			ObjectId objectId=ref.getObjectId();
			System.out.println("ID==="+ref.getObjectId());//AnyObjectId[7a35f1ccff7e5a85ce0677ec4b13e91f392db42c]
			 RevObject revObject =walk.parseAny(objectId);
			 if(revObject instanceof RevCommit){
				String name=((RevCommit) revObject).getAuthorIdent().getName();
				String getEmailAddress=((RevCommit) revObject).getAuthorIdent().getEmailAddress();
			 }else if(revObject instanceof RevTag){
					String message=((RevTag) revObject).getFullMessage();//标签
					System.out.println("getFullMessage"+message);
			 }
			String refName=repository.shortenRefName(ref.getName());
			System.out.println("shortenRefName==="+refName);
			}
			for (RevCommit revCommit2 : revcommit) {
				System.out.println("ID=="+revCommit2.getId());
				ObjectId id=revCommit2.getId();
				RevObject object=walk.parseAny(id);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
}
