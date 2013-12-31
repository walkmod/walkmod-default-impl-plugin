/* 
  Copyright (C) 2013 Raquel Pau and Albert Coroleu.
 
 Walkmod is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 Walkmod is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with Walkmod.  If not, see <http://www.gnu.org/licenses/>.*/
package org.walkmod.defaultimpl.visitors;

import java.util.LinkedList;
import java.util.List;
import org.walkmod.javalang.ASTManager;
import org.walkmod.javalang.ast.CompilationUnit;
import org.walkmod.javalang.ast.PackageDeclaration;
import org.walkmod.javalang.ast.body.ClassOrInterfaceDeclaration;
import org.walkmod.javalang.ast.body.MethodDeclaration;
import org.walkmod.javalang.ast.body.ModifierSet;
import org.walkmod.javalang.ast.body.TypeDeclaration;
import org.walkmod.javalang.ast.expr.AnnotationExpr;
import org.walkmod.javalang.ast.stmt.BlockStmt;
import org.walkmod.javalang.visitors.VoidVisitorAdapter;
import org.walkmod.walkers.VisitorContext;

public class DefaultImplGenerator extends VoidVisitorAdapter<VisitorContext> {

	private String implementationSuffix = "Impl";

	private String implementationPreffix = "";

	private PackageDeclaration packageDeclaration = null;

	public static final String GENERATED_IMPLEMENTATION = "gen_impl";

	public void visit(MethodDeclaration n, VisitorContext arg) {
		ClassOrInterfaceDeclaration impl = (ClassOrInterfaceDeclaration) arg
				.get(GENERATED_IMPLEMENTATION);
		List<AnnotationExpr> ann = new LinkedList<AnnotationExpr>();
		ann.add(ASTManager.getGeneratedAnnotation());
		MethodDeclaration aux = new MethodDeclaration(n.getJavaDoc(),
				n.getModifiers(), ann, n.getTypeParameters(), n.getType(),
				n.getName(), n.getParameters(), n.getArrayCount(),
				n.getThrows(), new BlockStmt());
		ASTManager.addMethodDeclaration(impl, aux);
	}

	public void visit(ClassOrInterfaceDeclaration n, VisitorContext arg) {
		if (n.isInterface()) {
			CompilationUnit cu = new CompilationUnit();
			cu.setPackage(packageDeclaration);
			List<TypeDeclaration> types = new LinkedList<TypeDeclaration>();
			ClassOrInterfaceDeclaration impl = new ClassOrInterfaceDeclaration(
					ModifierSet.PUBLIC, false, implementationPreffix
							+ n.getName() + implementationSuffix);
			types.add(impl);
			cu.setTypes(types);
			arg.put(GENERATED_IMPLEMENTATION, impl);
			super.visit(n, arg);
			arg.remove(GENERATED_IMPLEMENTATION);
			arg.addResultNode(cu);
		}
	}

	public String getImplementationSuffix() {
		return implementationSuffix;
	}

	public void setImplementationSuffix(String implementationSuffix) {
		this.implementationSuffix = implementationSuffix;
	}

	public String getImplementationPreffix() {
		return implementationPreffix;
	}

	public void setImplementationPreffix(String implementationPreffix) {
		this.implementationPreffix = implementationPreffix;
	}

	public PackageDeclaration getPackageDeclaration() {
		return packageDeclaration;
	}

	public void setPackageDeclaration(String packageDeclaration)
			throws Exception {
		if (packageDeclaration != null) {
			this.packageDeclaration = new PackageDeclaration(
					ASTManager.parseName(packageDeclaration));
		}
	}
}
