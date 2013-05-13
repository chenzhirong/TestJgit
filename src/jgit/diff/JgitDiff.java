package jgit.diff;

import java.io.ByteArrayOutputStream;
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
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
/*
 * 
 * Child 子版本号
 * Parent 父版本号
 */
public class JgitDiff {
	
	public JgitDiff() {
	}
	static String URL="D:/cfrManage/ConfigFile/.git";
	static Git git;
	public static Repository repository;
	public static void main(String[] args) {
		JgitDiff jgitDiff = new JgitDiff();
		//jgitDiff.getHistoryInfo("设备配置文件管理/思科/192.168.0.248/192.168.0.248config.txt");
		jgitDiff.diffMethod("60933e5d578568e65d2cb29da24350daeb2bf2b3","f092789690370c7ce3ddfbd7b73ee402198cc60b");
	}
	/*
	 * 
	 */
	public void diffMethod(String Child, String Parent){
		try {
			git=Git.open(new File("D:/cfrManage/ConfigFile/.git"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		repository=git.getRepository();
		ObjectReader reader = repository.newObjectReader();
		CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
	
		try {
			ObjectId old = repository.resolve(Child + "^{tree}");
			ObjectId head = repository.resolve(Parent+"^{tree}");
					oldTreeIter.reset(reader, old);
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, head);
			List<DiffEntry> diffs= git.diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .call();
			
			 ByteArrayOutputStream out = new ByteArrayOutputStream();
			    DiffFormatter df = new DiffFormatter(out);
			    df.setRepository(git.getRepository());
			
			for (DiffEntry diffEntry : diffs) {
		         df.format(diffEntry);
		         String diffText = out.toString("UTF-8");
		         System.out.println(diffText);
		       //  out.reset();
			}
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	// 读取历史记录
		public static void getHistoryInfo(String path) {
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
					String author = revCommit.getAuthorIdent().getName();
					String mail = revCommit.getAuthorIdent().getEmailAddress();
					Date date = revCommit.getAuthorIdent().getWhen();
					// String time=String.valueOf(date);
					SimpleDateFormat simple = new SimpleDateFormat(
							"yyyy年MM月dd日hh:mm:ss");
					String time = simple.format(date);
					String version = revCommit.getName();
					String message = revCommit.getFullMessage();
                    System.out.println(version);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
//	private static String getDiff(String file1, String file2) {
//	    OutputStream out = new ByteArrayOutputStream();
//	    try {
//	        RawText rt1 = new RawText(new File(file1));
//	        RawText rt2 = new RawText(new File(file2));
//	        EditList diffList = new EditList();
//	        diffList.addAll(differ.diff(COMP, rt1, rt2));
//	        new DiffFormatter(out).format(diffList, rt1, rt2);
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//	    return out.toString();
//	}
}

