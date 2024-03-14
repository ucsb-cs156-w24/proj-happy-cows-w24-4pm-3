import { Button, Form } from 'react-bootstrap';
import { useForm } from 'react-hook-form'
import { useNavigate } from 'react-router-dom'


function AnnouncementForm({ initialContents, submitAction, buttonLabel = "Create" }) {

    // Stryker disable all
    const {
        register,
        formState: { errors },
        handleSubmit,
    } = useForm(
        { defaultValues: initialContents || {}, }
    );
    // Stryker restore all


    const navigate = useNavigate();

    const testIdPrefix = "AnnouncementForm";


    // Stryker disable next-line Regex
    const isodate_regex = /(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d)|(\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d)/i;

    // Stryker disable next-line all
    //const yyyyq_regex = /((19)|(20))\d{2}[1-4]/i; 

    return (

        <Form onSubmit={handleSubmit(submitAction)}>

            {initialContents && (
                <Form.Group className="mb-3" >
                    <Form.Label htmlFor="id">Id</Form.Label>
                    <Form.Control
                    // Stryker disable next-line all
                        data-testid={testIdPrefix + "-id"}
                        id="id"
                        type="text"
                        {...register("id")}
                        value={initialContents.id}
                        disabled
                    />
                </Form.Group>
            )}

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="start">Start</Form.Label>
                <Form.Control
                // Stryker disable next-line all
                    data-testid={testIdPrefix + "-start"}
                    id="start"
                    type="datetime-local"
                    isInvalid={Boolean(errors.start)}
                    {...register("start", {
                        required: "Start is required.",
                        pattern: isodate_regex
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.start && 'Start is required (in ISO format).'}
                </Form.Control.Feedback>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label htmlFor="end">End</Form.Label>
                <Form.Control
                // Stryker disable next-line all
                    data-testid={testIdPrefix + "-end"}
                    id="end"
                    type="datetime-local"
                    isInvalid={Boolean(errors.end)}
                    // Stryker disable next-line all
                    {...register("end", {
                        pattern: isodate_regex
                    })}
                />
            </Form.Group>

            <Form.Group className="mb-3">
                <Form.Label htmlFor="announcement">Announcement</Form.Label>
                <Form.Control
                    as="textarea"
                    // Stryker disable next-line all
                    data-testid={testIdPrefix + "-announcement"}
                    id="announcement"
                    rows={5}
                    isInvalid={Boolean(errors.announcement)}
                    {...register("announcement", {
                        required: "Announcement is required."
                    })}
                />
                <Form.Control.Feedback type="invalid">
                    {errors.announcement?.message}
                </Form.Control.Feedback>
            </Form.Group>

            <Button
                type="submit"
                // Stryker disable next-line all
                data-testid={testIdPrefix + "-submit"}
            >
                {buttonLabel}
            </Button>
            <Button
                variant="Secondary"
                onClick={() => navigate(-1)}
                // Stryker disable next-line all
                data-testid={testIdPrefix + "-cancel"}
            >
                Cancel
            </Button>

        </Form>
    )
}

export default AnnouncementForm;