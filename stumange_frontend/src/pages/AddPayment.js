import React from 'react';
import { Container, Card, Alert } from 'react-bootstrap';

const AddPayment = () => {
    return (
        <Container fluid>
            <Card>
                <Card.Body>
                    <Alert variant="info">
                        <i className="fas fa-info-circle me-2"></i>
                        添加缴费功能正在开发中，敬请期待！
                    </Alert>
                </Card.Body>
            </Card>
        </Container>
    );
};

export default AddPayment;
