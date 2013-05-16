package example;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.util.StringUtils;

public class GitUtil {

    private final static String GIT = ".git";
    
    public static void main(String[] args) {
    	try {
//    		List<DiffEntry> diff=GitUtil.getLog("D:/cfrManage/ConfigFile/.git", "8ffa560113bc4578c7ac3117726c82baf9c1e57f");
//    		for (DiffEntry diffEntry : diff) {
//				System.out.println(diffEntry.getChangeType().toString());
//				System.out.println("=================================");
//			}
//    		boolean flg=GitUtil.rollBackPreRevision("D:/cfrManage/ConfigFile/.git", "9d557d4891ddc428125f2fa334d9410aea4d34c9");
//    		System.out.println(flg);
    		//String version=GitUtil.commitToGitRepository("D:/cfrManage/ConfigFile/.git", null);
	       // System.out.println(version);
    	} catch (Exception e) {
			e.printStackTrace();
		}
	}
 

    /**
     * ���ļ��б��ύ��git�ֿ���
     * @param gitRoot git�ֿ�Ŀ¼
     * @param files ��Ҫ�ύ���ļ��б�
     * @return ���ر����ύ�İ汾��
     * @throws IOException 
     */
    public static String commitToGitRepository(String gitRoot, List<String> files) throws Exception {
        //if (StringUtils.isEmptyOrNull(gitRoot) || files != null || files.size() > 0) {
    	if(true){
            File rootDir = new File(gitRoot);
            //��ʼ��git�ֿ�
            if (new File(gitRoot + File.separator + GIT).exists() == false) {
                Git.init().setDirectory(rootDir).call();
            }
            //��git�ֿ�
            Git git = Git.open(rootDir);
            //�ж��Ƿ��б��޸Ĺ����ļ�
            List<DiffEntry> diffEntries = git.diff()
                .setPathFilter(PathFilterGroup.createFromStrings("D:/cfrManage/ConfigFile/�豸�����ļ�����/˼��/192.168.0.248"))
                .setShowNameAndStatusOnly(true).call();
            if (diffEntries == null || diffEntries.size() == 0) {
                throw new Exception("�ύ���ļ����ݶ�û�б��޸ģ������ύ");
            }
            //���޸Ĺ����ļ�
            List<String> updateFiles=new ArrayList<String>();
            ChangeType changeType;
            for(DiffEntry entry : diffEntries){
                changeType = entry.getChangeType();
                switch (changeType) {
                    case ADD:
                        updateFiles.add(entry.getNewPath());
                        break;
                    case COPY:
                        updateFiles.add(entry.getNewPath());
                        break;
                    case DELETE:
                        updateFiles.add(entry.getOldPath());
                        break;
                    case MODIFY:
                        updateFiles.add(entry.getOldPath());
                        break;
                    case RENAME:
                        updateFiles.add(entry.getNewPath());
                        break;
                    }
            }
            //���ļ��ύ��git�ֿ��У������ر����ύ�İ汾��
            AddCommand addCmd = git.add();
            for (String file : updateFiles) {
                addCmd.addFilepattern(file);
            }
            addCmd.call();

            CommitCommand commitCmd = git.commit();
            for (String file : updateFiles) {
                commitCmd.setOnly(file);
            }
            RevCommit revCommit = commitCmd.setCommitter(Constants.DEFAULT_REMOTE_NAME, Constants.GIT_COMMITTER_EMAIL_KEY)
                .setMessage("publish").call();
            return revCommit.getName();
        }
        return null;
    }

    /**
     * ��git�ֿ����ݻع���ָ���汾����һ���汾
     * @param gitRoot �ֿ�Ŀ¼
     * @param revision ָ���İ汾��
     * @return true,�ع��ɹ�,����flase
     * @throws IOException
     */
    public static boolean rollBackPreRevision(String gitRoot, String revision) throws IOException {
        Git git = Git.open(new File(gitRoot));
        Repository repository = git.getRepository();
        RevWalk walk = new RevWalk(repository);
        ObjectId objId = repository.resolve(revision);
        RevCommit revCommit = walk.parseCommit(objId);
        String preVision = revCommit.getParent(0).getName();
        try {
			git.reset().setMode(ResetType.HARD).setRef(preVision).call();
		} catch (CheckoutConflictException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
        repository.close();
        return true;
    }

    /**
     * ��ѯ�����ύ����־
     * @param gitRoot git�ֿ�
     * @param revision  �汾��
     * @return 
     * @throws Exception
     */
    public static List<DiffEntry> getLog(String gitRoot, String revision) throws Exception {
        Git git = Git.open(new File(gitRoot));
        Repository repository = git.getRepository();

        ObjectId objId = repository.resolve(revision);
        Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
        Iterator<RevCommit> iter = allCommitsLater.iterator();
        RevCommit commit = iter.next();
        TreeWalk tw = new TreeWalk(repository);
        tw.addTree(commit.getTree());
        commit = iter.next();
        if (commit != null)
            tw.addTree(commit.getTree());
        else
            return null;
        tw.setRecursive(true);
        RenameDetector rd = new RenameDetector(repository);
        rd.addAll(DiffEntry.scan(tw));
        return rd.compute();
    }
}