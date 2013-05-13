package example;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.CommitBuilder;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.WalkTransport;
import org.eclipse.jgit.treewalk.TreeWalk;

/*
 * jgit 历史记录 和  标签信息
 */
public class LogAndTag {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LogAndTag l = new LogAndTag();
		// System.out.println(l.getLog());
		System.out.println(l.getTag("ce2110fa2cf47c60868e1b0284c62968892e6d20"));
	//	l.createTag("ce2110fa2cf47c60868e1b0284c62968892e6d20", "123", "rongzai521");
		//l.getRef();
		//l.createTag();
	}
	/**
	 * 得到标签名
	 * 通过Tag  objectID 得到revCommit   ObjectID
	 */
		public static String getTag(String version) {
			RevWalk walk=null;
			try {
				Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
				List<Ref> ref = git.tagList().call();
				for (int i = 0; i < ref.size(); i++) {
					Ref gitRef = ref.get(i);
					ObjectId refId = gitRef.getObjectId();
					walk = new RevWalk(git.getRepository());
					RevObject revObject = walk.parseAny(refId);
					if(revObject instanceof RevCommit){
						
					}else if(revObject instanceof RevTag){
						RevCommit revCommit=walk.parseCommit(revObject);
						
						if(version.equals(revCommit.getName())){
							return ((RevTag) revObject).getTagName();
						}
					} else if(revObject instanceof RevTree){
						
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		/**
		 * 反回指定版本ce2110fa2cf47c60868e1b0284c62968892e6d20
		 * 
		 */
		public void getRef() {
			RevWalk walk=null;
				try {
					Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
					Ref ref=git.getRepository().getRef("v1.1");
					System.out.println(ref.getObjectId());
					git.revert().include(ref).call().reset();
					git.reset().addPath("设备配置文件管理/思科/192.168.0.248/192.168.0.248config.txt").call();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoMessageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnmergedPathsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ConcurrentRefUpdateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WrongRepositoryStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		
/**
 * 得到标签名ce2110fa2cf47c60868e1b0284c62968892e6d20
 * 通过Tag  objectID 得到revCommit   ObjectID
 */
	public void getTag() {
		RevWalk walk=null;
		try {
			Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
			List<Ref> ref = git.tagList().call();
			for (int i = 0; i < ref.size(); i++) {
				Ref gitRef = ref.get(i);
				//git.reset().setRef("1.1");
				ObjectId refId = gitRef.getObjectId();
				walk = new RevWalk(git.getRepository());
				RevObject revObject = walk.parseAny(refId);
				if(revObject instanceof RevCommit){
					
				}else if (revObject instanceof RevTag) {
					 RevCommit revCommit=walk.parseCommit(revObject);
					 System.out.println(revCommit.getName());
				} else if(revObject instanceof RevTree){
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	/**
	 * 对指定版本打标签
	 */
	public static void createTag(String version, String tagName, String message) {
		try {
			Git git = Git.open(new File("d:/cfrmanage/configfile/"));
			RevWalk walk = new RevWalk(git.getRepository());
			ObjectId obje = git.getRepository().resolve(version);
			RevCommit commit = walk.parseCommit(obje);
			try {
//				git.tag().setObjectId(commit).setName(tagName)
//						.setMessage(message).call();
				git.tagDelete().setTags("v1.6").call();
			} catch (ConcurrentRefUpdateException e) {
			} catch (InvalidTagNameException e) {
				e.printStackTrace();
			} catch (NoHeadException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 对指定版本打标签
	 */
	public void createTag(String version){
		try {
			Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
			RevWalk walk=new RevWalk(git.getRepository());
		  ObjectId obje=	git.getRepository().resolve(version);
	      RevCommit commit=	walk.parseCommit(obje);
			try {
				git.tag().setObjectId(commit).setName("v2.0").setMessage("version 2.0").call();
			} catch (ConcurrentRefUpdateException e) {
			} catch (InvalidTagNameException e) {
				e.printStackTrace();
			} catch (NoHeadException e) {
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
/**
 * 修改提交信息
 */
	public void UpdataMessage(){
			
			try {
				Git git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
				git.commit().setOnly("26971c14ca829b822bc48dc887f241635e6bfbdb").setMessage("yangyang");
				// TODO Auto-generated catch block
			} catch (IOException e) {
			}
	}

	public String getLog() {
			Git git;
				try {
					git = Git.open(new File("D:/cfrManage/ConfigFile/.git"));
					Iterable<RevCommit> revCommit = git.log().call();
					
					for (RevCommit revCommit2 : revCommit) {
						String Obje=revCommit2.getName();
						RevWalk walk=new RevWalk(git.getRepository());
						ObjectId objectId=git.getRepository().resolve(Obje);
						RevTag revTag=walk.parseTag(objectId);
						if(revTag instanceof RevTag){
							System.out.println(revTag.getName());
							System.out.println(((RevTag) revTag).getFullMessage());
						}
		            }
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoHeadException e) {
					e.printStackTrace();
				} catch (GitAPIException e) {
					e.printStackTrace();
				}
				
				return null;
	}
}
