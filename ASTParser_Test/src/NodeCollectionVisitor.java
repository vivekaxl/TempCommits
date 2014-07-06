import java.util.ArrayList;
import java.util.function.Predicate;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

class NodeCollectionVisitor extends ASTVisitor {
		final ArrayList<ASTNode> nodes = new ArrayList<ASTNode>();
		private final Predicate<ASTNode> condition;
		
		NodeCollectionVisitor(Predicate<ASTNode> condition) {
			this.condition = condition;
		}
		public void preVisit(ASTNode node) {
			//System.out.println(node.toString());
			if(condition.test(node))
				nodes.add(node);
		}

	}