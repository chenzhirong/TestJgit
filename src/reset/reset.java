package reset;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
/*
 * �汾�ع�
 */
public class reset {
	public static void main(String[] args) {
		reset r=new reset();
		boolean flg=r.reset("D:/cfrManage/ConfigFile","ce2110fa2cf47c60868e1b0284c62968892e6d20");
        System.out.println(flg);
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
			git.reset().setMode(ResetType.HARD).setRef(revision).call();
		} catch (CheckoutConflictException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}  
        repository.close();  
        return true;  
    }  
    //�ع���ָ���汾
    public static boolean reset(String gitRoot, String revision)  {  
    	try {
    		Git git = Git.open(new File(gitRoot));  
    		git.reset().setMode(ResetType.HARD).setRef(revision).call();
    	} catch (CheckoutConflictException e) {
    		e.printStackTrace();
    	} catch (GitAPIException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
			e.printStackTrace();
		}  
    	return true;  
    }  

}
