package example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.Tree;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.WalkTransport;

public class JGit {
	static Git git;

    public static void main(String[] args) throws Throwable {
    	JGit j=new JGit();
       j.test();
    }
    
    public void test(){
    	 File root = new File("d:/cfrmanage/configfile/.git");
    	 try {
			Git git=Git.open(root);
	        Repository repository=git.getRepository();
	        ObjectId obj=repository.resolve("e306c34f9345fbc784d079cd95e6d3df4057c815");
	        RevWalk revWalk=new RevWalk(repository);
	        ObjectId revCommit= revWalk.parseCommit(obj);
	       System.out.println(revCommit.getName());
	       // System.out.println(revCommit.getFullMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public static void gitInte(){
    	 File root = new File("c:/cfrmanage/configfile/.git");
         if(!root.exists())
             root.mkdir();
         if(!root.exists()) {//如果已经初始化过,那肯定有.git文件夹
             try {
				Git.init().setDirectory(root).call();
				  git = Git.open(root); //打开git库
			} catch (GitAPIException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
         }
         if(git==null){
        	 try {
        		 Git.init().setDirectory(root).call();
				  git = Git.open(root); //打开git库
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
         }
    }
    public static void gitPush() throws IOException, NoFilepatternException, GitAPIException{
    	//好吧,随便写一个文件进去先
        File newFile = new File("gitme/rongzai.java");
        BufferedWriter fw=new BufferedWriter(new FileWriter(newFile));
        fw.write("rongzai");
         //添加文件咯,相当于 git add .
    //   git.add().addFilepattern(".+").call();
                //然后当然是提交啦,相当于 git commit
        git.commit().setCommitter("wendal", "wendal1985@gmail.com").setMessage("updata jgit!").call();
        git.push().call();
        
        //接下来,我们看看log信息
//        for(RevCommit revCommit : git.log().addPath("gitme/rongzai.java").call()){
//            System.out.println(revCommit.getName());
//            System.out.println(revCommit.getFullMessage());
//            System.out.println(revCommit.getCommitterIdent().getName() + " " + revCommit.getCommitterIdent().getEmailAddress());
//        }
    }

}