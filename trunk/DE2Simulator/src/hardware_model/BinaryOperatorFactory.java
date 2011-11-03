package hardware_model;

public class BinaryOperatorFactory {

	public static Operator createNewBinaryOperator(String operator, int precedence) {
		if ( operator.equals("&") ){
			return new BitwiseAndOperator(precedence);
		}
		else if ( operator.equals("&&") ){
			return new LogicAndOperator(precedence);
		}
		else if ( operator.equals("|") ){
			return new BitwiseOrOperator(precedence);
		}
		else if ( operator.equals("||") ){
			return new LogicOrOperator(precedence);
		}
		else if ( operator.equals("^") ){
			return new BitwiseXorOperator(precedence);
		}
		else if ( operator.equals("+") ){
			return new BitwiseAddOperator(precedence);
		}
		else if ( operator.equals("-") ){
			return new BitwiseSubtractOperator(precedence);
		}
		else if ( operator.equals(">>") ){
			return new LogicShiftRightOperator(precedence);
		}
		else if ( operator.equals(">>>") ){
			return new ArihtimeticShiftRightOperator(precedence);
		}
		else if ( operator.equals("<<") ){
			return new LogicShiftLeftOperator(precedence);
		}
		else{
			return null;
		}
	}

}
