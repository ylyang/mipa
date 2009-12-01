package net.sourceforge.mipa.predicatedetection;

import java.io.Serializable;

public class Connector extends Composite implements Serializable {

    private static final long serialVersionUID = 182840393628790171L;

    private NodeType operator;
    
    private boolean flagForUniversal = false;
    
    public Connector(NodeType type, String name) {
        super(type, name);
        // TODO Auto-generated constructor stub
    }

    public void setOperator(String operator) {
        if(operator.equals(NodeType.CONJUNCTION.toString()))
        {
            this.operator = NodeType.CONJUNCTION;
        }
        else if(operator.equals(NodeType.DISJUNCTION.toString()))
        {
            this.operator = NodeType.DISJUNCTION;
        }
        else if(operator.equals(NodeType.EXISTENTIAL.toString()))
        {
            this.operator = NodeType.EXISTENTIAL;
        }
        else if(operator.equals(NodeType.IMPLY.toString()))
        {
            this.operator = NodeType.IMPLY;
        }
        else if(operator.equals(NodeType.NOT.toString()))
        {
            this.operator = NodeType.NOT;
        }
        else if(operator.equals(NodeType.UNIVERSAL.toString()))
        {
            this.operator = NodeType.UNIVERSAL;
        }
        else
        {
            System.out.println("Operator "+ operator +" not defined!");
        }
    }

    public NodeType getOperator() {
        return operator;
    }
    
    public void update()
    {
        setLastValue(nodeValue);
        NodeType nodeType = getNodeType();
        switch(nodeType)
        {
            case QUANTIFIER:
            {
                Composite composite = (Composite)getChildren().get(0);
                boolean value = composite.getNodeValue();
                switch(operator)
                {
                    case UNIVERSAL:
                    {
                        if(flagForUniversal == false)
                        {
                            nodeValue = true;
                            flagForUniversal = true;
                        }
                        if(value == false)
                        {
                            nodeValue = false;
                        }
                        break;
                    }
                    case EXISTENTIAL:
                    {
                        if(value == true)
                        {
                            nodeValue = true;
                        }
                        break;
                    }
                    default:
                    {
                        System.out.println("Non-defined quantifier value "+operator);
                        break;
                    }
                }
                break;
            }
            case UNARY:
            {
                Composite composite = (Composite)getChildren().get(0);
                boolean value = composite.getNodeValue();
                switch(operator)
                {
                    case NOT:
                    {
                        if(value == true)
                        {
                            nodeValue = false;
                        }
                        else
                        {
                            nodeValue = true;
                        }
                        break;
                    }
                    default:
                    {
                        System.out.println("Non-defined unary value "+operator);
                        break;
                    }
                }
                break;
            }
            case BINARY:
            {
                Composite compositeLeft = (Composite)getChildren().get(0);
                boolean valueLeft = compositeLeft.getNodeValue();
                Composite compositeRight = (Composite)getChildren().get(1);
                boolean valueRight = compositeRight.getNodeValue();
                switch(operator)
                {
                    case CONJUNCTION:
                    {
                        nodeValue = (valueLeft && valueRight);
                        break;
                    }
                    case DISJUNCTION:
                    {
                        nodeValue = (valueLeft || valueRight);
                        break;
                    }
                    case IMPLY:
                    {
                        nodeValue = (!valueLeft||valueRight);
                        break;
                    }
                    default:
                    {
                        System.out.println("Non-defined bianry value "+operator);
                        break;
                    }
                }
                break;
            }
            default:
            {
                System.out.println("Non-defined operator "+operator);
                break;
            }
        }
    }
}